import { useEffect, useRef } from "react";  // React에서 useEffect, useRef 기능을 가져옴
import tourIcon from '../assets/tour.png';
import festivalIcon from '../assets/festival.png';
import gymIcon from '../assets/gym.png';
import artIcon from '../assets/art.png';
import foodIcon from '../assets/food.png';
import dessertIcon from '../assets/dessert.png';
import marketIcon from '../assets/market.png';
import pharmacyIcon from '../assets/pharmacy.png';
import buildingIcon from '../assets/building.png';
import cultureIcon from '../assets/culture.png';
function KakaoMap({viewType}) {       // 함수 시작
    const mapRef = useRef(null);        // 지도를 그릴 div를 나중에 찾기 위한 변수, 처음엔 비어있음(null)
    const getMarkerIcon = (content) => {
        if (content.categoryId) {
            const contentIconMap = {
                1: tourIcon,      // 관광
                2: festivalIcon,  // 축제
                3: cultureIcon,   // 문화재
                4: gymIcon,       // 공공 체육시설
                5: artIcon,       // 건축 미술
                6: artIcon,       // 공공 미술
            };
            return contentIconMap[content.categoryId];
        }
        if (content.shopCategory) {
            const shopIconMap = {
                'FOOD': foodIcon,
                'CAFE': dessertIcon,
                'STORE': marketIcon,
                'MEDICAL': pharmacyIcon,
                'LIFE': buildingIcon,
                'ETC': buildingIcon,
            };
            return shopIconMap[content.shopCategory];
        }
    };
    useEffect(() => {       // 함수 시작
        const script = document.createElement("script");        // scipt를 만들고
        script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_MAP_KEY}&autoload=false`; // 연결할 스크립트 url
        script.onload = () => {         // script가 전부 실행되면 실행할 코드 시작
            window.kakao.maps.load(() => {  // 맵을 띄움
                navigator.geolocation.getCurrentPosition(   // 브라우저의 GPS 기능 실행, 사용자한테 위치 권한 팝업 띄움
                    (position) => { // GPS 성공시 실행
                        const lat = 35.1798;   // 진주시청으로 고정
                        const lng = 128.1076;  // 진주시청으로 고정
                        const currentlat = lat;
                        const currentlng = lng;
                        // const lat = position.coords.latitude;   // GPS로 받은 사용자 위치(위도)
                        // const lng = position.coords.longitude;  // GPS로 받은 사용자 위치(경도)

                        const options = {
                            center: new window.kakao.maps.LatLng(lat, lng), // 진주 시청으로 고정
                            level: 3,
                        };
                        // const options = {       // 지도 옵션 설정
                        //     center: new window.kakao.maps.LatLng(lat, lng), // 현재 GPS의 위도 경도를 화면 가운데
                        //     level: 3,                                       // 화면 확대 레벨
                        // };
                        const map = new window.kakao.maps.Map(mapRef.current, options); // 맵 화면 띄울변수
                        let contentMarkers = [];  // contents 마커들을 저장할 배열 추가
                        let infowindows = [];   // 인포윈도우(인증 창) 배열
                        let authContents = [];   // 인증 가능한 배열
                        console.log("요청 좌표", lat, lng);

                        Promise.all([   // 현재위치를 기준으로 주변 컨텐츠, 인증가능한 컨텐츠 호출
                            fetch(`http://localhost:8080/api/safety/contents?lat=${lat}&lng=${lng}&radius=1000`).then(res => res.json()),
                            fetch(`http://localhost:8080/api/safety/auth-contents?lat=${currentlat}&lng=${currentlng}&radius=50`).then(res => res.json())
                        ]).then(([contents, auth]) => {
                            authContents = auth;  // 저장해두면 클릭 이벤트에서도 쓸 수 있음
                            contents.forEach(content => {
                                const title = content.contentsTitle ?? content.shopTitle;
                                const id = content.contentsId ?? content.shopId;

                                // 아이콘 추가
                                const iconUrl = getMarkerIcon(content);
                                const markerImage = iconUrl
                                    ? new window.kakao.maps.MarkerImage(
                                        iconUrl,
                                        new window.kakao.maps.Size(27, 44)
                                    )
                                    : null;
                                const marker = new window.kakao.maps.Marker({
                                    position: new window.kakao.maps.LatLng(content.lat, content.lng),
                                    title: title,
                                    image: markerImage
                                });

                                marker.setMap(map);
                                contentMarkers.push(marker);
                                // 반경이내에 있으면 인증 가능
                                const isAuthable = authContents.some(auth =>
                                    (auth.contentsId && auth.contentsId === content.contentsId) ||
                                    (auth.shopId && auth.shopId === content.shopId)
                                );

                                const infowindow = new window.kakao.maps.InfoWindow({
                                    content: `
                                        <div style="padding:10px; min-width:200px">
                                            <b>${title}</b><br/>
                                            ${isAuthable
                                            ? `<button id="auth-btn-${id}" ...>인증하기</button>`
                                            : `<p style="color:gray">50m 밖 - 인증 불가</p>`
                                        }
                                        </div>
                                    `
                                });
                                infowindows.push(infowindow);   // 인증창 저장

                                // 마커 클릭 시 인포윈도우 열기
                                window.kakao.maps.event.addListener(marker, 'click', () => {
                                    infowindows.forEach(iw => iw.close());  // 기존 인포윈도우 전부 닫기
                                    infowindow.open(map, marker);

                                    setTimeout(() => {
                                        const btn = document.getElementById(`auth-btn-${id}`);
                                        if (btn) {
                                            btn.onclick = async () => {
                                                console.log("인증하기 클릭:", title);
                                                // TODO: 인증 API 호출
                                                alert(`${title} 인증 완료!`);
                                            };
                                        }
                                    }, 100);
                                });
                            });
                        });

                        const makerPosition = new window.kakao.maps.LatLng(lat, lng);     // 현재 위치의 마커 위치 설정
                        const maker = new window.kakao.maps.Marker({                      // 지도에 마커 생성
                            position: makerPosition
                        });
                        maker.setMap(map);      // 지도에 마커 표시

                        let clickMaker = null;    // 클릭 마커 선언(처음은 없음)
                        window.kakao.maps.event.addListener(map, 'click', async (mouseEvent) => {
                            const lat = mouseEvent.latLng.getLat(); // 클릭한 위도
                            const lng = mouseEvent.latLng.getLng(); // 클릭한 경도

                            if (clickMaker) { clickMaker.setMap(null); }        // 기존 클릭 마커 제거

                            contentMarkers.forEach(m => m.setMap(null));
                            contentMarkers = [];    // 클릭할때 마다 이전 contents마커 전부 제거
                            infowindows.forEach(iw => iw.close());  // 기존 인포윈도우 전부 닫기
                            infowindows = [];

                            clickMaker = new window.kakao.maps.Marker({
                                position: new window.kakao.maps.LatLng(lat, lng),   // 새 마커 생성
                            });
                            clickMaker.setMap(map);

                            const response = await fetch("http://localhost:8080/api/safety/", {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json",
                                },
                                body: JSON.stringify({
                                    lat: lat,
                                    lng: lng,
                                    radius: 500,    // 반경 500m
                                }),         // http://localhost:8080/api/safety/에 POST매핑으로 헤더에 콘텐츠 타입을 json타입으로 하고 전달은 위도,경도, 그에따른 반경 500로 전달
                            });
                            const response2 = await fetch(`http://localhost:8080/api/safety/contents?lat=${lat}&lng=${lng}&radius=1000`);

                            const contents = await response2.json();
                            contents.forEach(content => {
                                const title = content.contentsTitle ?? content.shopTitle;
                                const id = content.contentsId ?? content.shopId;
                                const iconUrl = getMarkerIcon(content);
                                const markerImage = iconUrl
                                    ? new window.kakao.maps.MarkerImage(
                                        iconUrl,
                                        new window.kakao.maps.Size(27, 44)
                                    )
                                    : null;
                                const marker = new window.kakao.maps.Marker({
                                    position: new window.kakao.maps.LatLng(content.lat, content.lng),
                                    title: title,
                                    image: markerImage
                                });
                                marker.setMap(map);
                                contentMarkers.push(marker);

                                const isAuthable = authContents.some(auth =>
                                    (auth.contentsId && auth.contentsId === content.contentsId) ||
                                    (auth.shopId && auth.shopId === content.shopId)
                                );

                                const infowindow = new window.kakao.maps.InfoWindow({
                                    content: `
                                        <div style="padding:10px; min-width:200px">
                                            <b>${title}</b><br/>
                                            ${isAuthable
                                            ? `<button id="auth-btn-${id}"
                                            style="margin-top:8px; padding:4px 8px; cursor:pointer;
                                               background:#4CAF50; color:white; border:none; border-radius:4px">
                                            인증하기
                                            </button>`
                                            : `<p style="color:gray; font-size:12px; margin-top:8px">50m 밖 - 인증 불가</p>`
                                        }
                                         </div>
                                        `
                                });
                                infowindows.push(infowindow);

                                window.kakao.maps.event.addListener(marker, 'click', () => {
                                    infowindows.forEach(iw => iw.close());
                                    infowindow.open(map, marker);

                                    setTimeout(() => {
                                        const btn = document.getElementById(`auth-btn-${id}`);
                                        if (btn) {
                                            btn.onclick = async () => {
                                                console.log("인증하기 클릭:", title);
                                                alert(`${title} 인증 완료!`);
                                            };
                                        }
                                    }, 100);
                                });
                            });

                            console.log("클릭한 위치 - 위도:", lat, "경도:", lng);  // test용 위도 경도 확인차 콘솔 출력
                        });
                    },
                    () => {
                        const lat = 35.1798;
                        const lng = 128.1076;

                        const options = {
                            center: new window.kakao.maps.LatLng(lat, lng),
                            level: 3,
                        };
                        const map = new window.kakao.maps.Map(mapRef.current, options);
                        let contentMarkers = [];

                        // 초기 contents만 호출 (authContents 없음)
                        fetch(`http://localhost:8080/api/safety/contents?lat=${lat}&lng=${lng}&radius=1000`)
                            .then(res => res.json())
                            .then(contents => {
                                console.log("GPS 실패 초기 주변 컨텐츠", contents);
                                contents.forEach(content => {
                                    const marker = new window.kakao.maps.Marker({
                                        position: new window.kakao.maps.LatLng(content.lat, content.lng),
                                        title: content.contentsTitle
                                    });
                                    marker.setMap(map);
                                    contentMarkers.push(marker);
                                });
                            });

                        const markerPosition = new window.kakao.maps.LatLng(lat, lng);
                        const marker = new window.kakao.maps.Marker({ position: markerPosition });
                        marker.setMap(map);

                        let clickMaker = null;
                        window.kakao.maps.event.addListener(map, 'click', async (mouseEvent) => {
                            const lat = mouseEvent.latLng.getLat();
                            const lng = mouseEvent.latLng.getLng();

                            if (clickMaker) { clickMaker.setMap(null); }

                            contentMarkers.forEach(m => m.setMap(null));
                            contentMarkers = [];

                            clickMaker = new window.kakao.maps.Marker({
                                position: new window.kakao.maps.LatLng(lat, lng),
                            });
                            clickMaker.setMap(map);

                            const response = await fetch("http://localhost:8080/api/safety/", {
                                method: "POST",
                                headers: { "Content-Type": "application/json" },
                                body: JSON.stringify({ lat, lng, radius: 500 }),
                            });
                            const data = await response.json();
                            console.log("결과", data);

                            const response2 = await fetch(`http://localhost:8080/api/safety/contents?lat=${lat}&lng=${lng}&radius=1000`);
                            const contents = await response2.json();

                            // 마커만 찍고 인포윈도우 없음
                            contents.forEach(content => {
                                const marker = new window.kakao.maps.Marker({
                                    position: new window.kakao.maps.LatLng(content.lat, content.lng),
                                    title: content.contentsTitle
                                });
                                marker.setMap(map);
                                contentMarkers.push(marker);
                            });

                            console.log("클릭한 위치 - 위도:", lat, "경도:", lng);
                        });
                    }
                );
            });
        },
        document.head.appendChild(script);  // <head>태그에 <script>태그를 넣는다
    }, [viewType]);

    return <div ref={mapRef} style={{ width: "100%", height: "100%", position: "absolute" }} />;
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

참고
1. 500m 반경의 안심등급
2. 1km 반경의 contents 출력
3. 50m 반경의 인증 기능
*/