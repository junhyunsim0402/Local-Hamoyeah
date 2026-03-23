import { useEffect, useRef } from "react";  // React에서 useEffect, useRef 기능을 가져옴

function KakaoMap() {       // 함수 시작
    const mapRef = useRef(null);        // 지도를 그릴 div를 나중에 찾기 위한 변수, 처음엔 비어있음(null)

    useEffect(() => {       // 함수 시작
        const script = document.createElement("script");        // scipt를 만들고
        script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_MAP_KEY}&autoload=false`; // 연결할 스크립트 url
        script.onload = () => {         // script가 전부 실행되면 실행할 코드 시작
            window.kakao.maps.load(() => {  // 맵을 띄움
                navigator.geolocation.getCurrentPosition(   // 브라우저의 GPS 기능 실행, 사용자한테 위치 권한 팝업 띄움
                    (position) => { // GPS 성공시 실행
                        const lat = position.coords.latitude;   // GPS로 받은 사용자 위치(위도)
                        const lng = position.coords.longitude;  // GPS로 받은 사용자 위치(경도)

                        const options = {       // 지도 옵션 설정
                            center: new window.kakao.maps.LatLng(lat, lng), // 현재 GPS의 위도 경도를 화면 가운데
                            level: 3,                                       // 화면 확대 레벨
                        };
                        const map = new window.kakao.maps.Map(mapRef.current, options); // 맵 화면 띄울변수
                        const makerPosition = new window.kakao.maps.LatLng(lat, lng);     // 현재 위치의 마커 위치 설정
                        const maker = new window.kakao.maps.Marker({                      // 지도에 마커 생성
                            position: makerPosition
                        });
                        maker.setMap(map);      // 지도에 마커 표시
                        let clickMaker = null;    // 클릭 마커 선언(처음은 없음)
                        window.kakao.maps.event.addListener(map, 'click', (mouseEvent) => {
                            const lat = mouseEvent.latLng.getLat(); // 클릭한 위도
                            const lng = mouseEvent.latLng.getLng(); // 클릭한 경도

                            if (clickMaker) { clickMaker.setMap(null); }        // 기존 클릭 마커 제거

                            clickMaker = new window.kakao.maps.Marker({
                                position: new window.kakao.maps.LatLng(lat, lng),   // 새 마커 생성
                            });
                            clickMaker.setMap(map);

                            console.log("클릭한 위치 - 위도:", lat, "경도:", lng);  // test용 위도 경도 확인차 콘솔 출력
                        });
                    },
                    () => {         // GPS실패 했을 경우의 코드 시작( 진주 시청을 기본값 )
                        const options = {       // 지도 옵션 실행
                            center: new window.kakao.maps.LatLng(35.1798, 128.1076),
                            level: 3,
                        };
                        const map = new window.kakao.maps.Map(mapRef.current, options);
                        const markerPosition = new window.kakao.maps.LatLng(35.1798, 128.1076);
                        const marker = new window.kakao.maps.Marker({
                            position: markerPosition,
                        });
                        marker.setMap(map);
                        let clickMaker = null;    // 클릭 마커 선언(처음은 없음)
                        window.kakao.maps.event.addListener(map, 'click', (mouseEvent) => {
                            const lat = mouseEvent.latLng.getLat(); // 클릭한 위도
                            const lng = mouseEvent.latLng.getLng(); // 클릭한 경도

                            // 기존 클릭 마커 제거
                            if (clickMaker) { clickMaker.setMap(null); }

                            // 새 마커 생성
                            clickMaker = new window.kakao.maps.Marker({
                                position: new window.kakao.maps.LatLng(lat, lng),
                            });
                            clickMaker.setMap(map);

                            console.log("클릭한 위치 - 위도:", lat, "경도:", lng);
                        });
                    }
                );
            });
        };
        document.head.appendChild(script);  // <head>태그에 <script>태그를 넣는다
    }, []);

    return <div ref={mapRef} style={{ width: "100%", height: "500px" }} />;     // <div>안에 맵 화면을 그려주는 코드
}

export default KakaoMap;
/*
1. useEffect, useRef 가져오고
2. 맵 화면 변수 비어있게 선언 (mapRef = null)
3. script 만들고 API키 넣고
4. script가 다운로드 완료되면 (onload)
5. 카카오맵 초기화하고
6. GPS 실행
7. GPS 성공 → 사용자 위도/경도로 지도 옵션 설정 → 지도 띄움
   GPS 실패 → 진주 시청 좌표로 지도 띄움
8. script를 <head>에 넣어서 다운로드 시작 ← (3번 직후에 일어남)
9. div를 화면에 그려줌 (return)
10. 다른 파일에서 쓸 수 있게 KakaoMap으로 내보냄
*/