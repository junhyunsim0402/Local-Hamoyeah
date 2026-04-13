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
import peopleIcon from '../assets/people.png';
import groupIcon from '../assets/group.png';
// 함수 시작
function KakaoMap({ viewType, shopCategory, contentCategory, onAuthBtnClick, onScoreReady, onMarkerClick, moveToLocation }) {       // 함수 시작
    const mapRef = useRef(null);        // 지도를 그릴 div를 나중에 찾기 위한 변수, 처음엔 비어있음(null)
    const mapInstanceRef = useRef(null);        // map 객체 저장용
    const clustererRef = useRef(null);          // clusterer 저장용
    const contentMarkersRef = useRef([]);       // contents 마커 저장용
    const authContentsRef = useRef([]);         // 인증 가능 목록 저장용
    const shopCategoryRef = useRef('0');
    const contentCategoryRef = useRef('0');
    const moveToLocationRef = useRef(null);

    const groupNearbyContents = (contents) => {
        return contents.reduce((acc, item) => {
            const latFixed = parseFloat(item.lat).toFixed(4); // 약 11m 반경 그룹화
            const lngFixed = parseFloat(item.lng).toFixed(4);
            const key = `${latFixed}_${lngFixed}`;
            if (!acc[key]) acc[key] = [];
            acc[key].push(item);
            return acc;
        }, {});
    };
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

    // 마커 생성 공통 함수
    const createMarker = (contentGroup, map, clusterer) => {
        const items = Array.isArray(contentGroup) ? contentGroup : [contentGroup];
        const firstItem = items[0];
        const isMultiple = items.length > 1;

        const title = firstItem.contentsTitle ?? firstItem.shopTitle;
        const iconUrl = isMultiple ? groupIcon : getMarkerIcon(firstItem);
        const markerImage = new window.kakao.maps.MarkerImage(
            iconUrl,
            new window.kakao.maps.Size(32, 48)
        );

        const marker = new window.kakao.maps.Marker({
            position: new window.kakao.maps.LatLng(firstItem.lat, firstItem.lng),
            title: isMultiple ? `${firstItem.contentsTitle ?? firstItem.shopTitle} 외 ${items.length - 1}건` : (firstItem.contentsTitle ?? firstItem.shopTitle),
            image: markerImage
        });
        if (isMultiple) {
            // 1. 그룹화된 마커는 클러스터러에 넣지 않고 직접 지도에 표시
            marker.setMap(map);
        } else {
            // 2. 낱개 마커만 클러스터러에 넣어서 축소 시 숫자로 뭉치게 함
            clusterer.addMarker(marker);
        }
        //marker.setMap(map);
        contentMarkersRef.current.push(marker);



        window.kakao.maps.event.addListener(marker, 'click', () => {
            const isAuthable = authContentsRef.current.some(auth =>
                (auth.contentsId && auth.contentsId === firstItem.contentsId) ||
                (auth.shopId && auth.shopId === firstItem.shopId)
            );
            if (onMarkerClick) {
                onMarkerClick({
                    isMultiple: isMultiple, // 여러 개 뭉쳐있는지 여부
                    items: items,           // 뭉쳐있다면 그 리스트 전체
                    selectedPlace: firstItem, // 하나라면 그 장소 정보
                    targetType: firstItem.shopId ? 'SHOP' : 'CONTENT',
                    targetId: firstItem.shopId ?? firstItem.contentsId,
                    isAuthable
                });
            }
        });
    };

    // 카테고리 기준 마커 호출 공통 함수
    const fetchAndRenderMarkers = async (lat, lng, map, clusterer) => {
        const fetchPromises = [];

        if (shopCategoryRef.current === '0') {
            fetchPromises.push(
                fetch(`http://localhost:8080/api/safety/contents?lat=${lat}&lng=${lng}&radius=1000`)
                    .then(res => res.json())
                    .then(contents => contents.filter(c => c.shopId))
            );
        } else if (shopCategoryRef.current !== 'NONE') {
            fetchPromises.push(
                fetch(`http://localhost:8080/api/safety/shop/category?lat=${lat}&lng=${lng}&radius=1000&shopCategory=${shopCategoryRef.current}`)
                    .then(res => res.json())
            );
        }

        if (contentCategoryRef.current === '0') {
            fetchPromises.push(
                fetch(`http://localhost:8080/api/safety/contents?lat=${lat}&lng=${lng}&radius=1000`)
                    .then(res => res.json())
                    .then(contents => contents.filter(c => c.contentsId))
            );
        } else if (contentCategoryRef.current !== 'NONE') {
            fetchPromises.push(
                fetch(`http://localhost:8080/api/safety/contents/category?lat=${lat}&lng=${lng}&radius=1000&categoryId=${contentCategoryRef.current}`)
                    .then(res => res.json())
            );
        }

        Promise.all(fetchPromises).then(results => {
            const allContents = results.flat();

            // 그룹화 로직 적용
            const grouped = groupNearbyContents(allContents);
            Object.values(grouped).forEach(group => {
                createMarker(group, map, clusterer);
            });
        });
    };


    // 마커 전부 제거 공통 함수
    const clearMarkers = (clusterer) => {
        contentMarkersRef.current.forEach(m => m.setMap(null));
        contentMarkersRef.current = [];
        if (clusterer) {
            clusterer.clear();
        }
    };

    useEffect(() => {       // 함수 시작
        const script = document.createElement("script");        // scipt를 만들고
        script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_MAP_KEY}&autoload=false&libraries=clusterer`;
        script.onload = () => {         // script가 전부 실행되면 실행할 코드 시작
            window.kakao.maps.load(() => {  // 맵을 띄움
                navigator.geolocation.getCurrentPosition(   // 브라우저의 GPS 기능 실행, 사용자한테 위치 권한 팝업 띄움
                    (position) => { // GPS 성공시 실행
                        const lat = 35.1798;   // 진주시청으로 고정
                        const lng = 128.1076;  // 진주시청으로 고정
                        // const lat = position.coords.latitude;   // GPS로 받은 사용자 위치(위도)
                        // const lng = position.coords.longitude;  // GPS로 받은 사용자 위치(경도)
                        const currentlat = lat;
                        const currentlng = lng;

                        const options = {
                            center: new window.kakao.maps.LatLng(lat, lng), // 진주 시청으로 고정
                            level: 3,
                        };
                        // const options = {       // 지도 옵션 설정
                        //     center: new window.kakao.maps.LatLng(lat, lng), // 현재 GPS의 위도 경도를 화면 가운데
                        //     level: 3,                                       // 화면 확대 레벨
                        // };
                        const map = new window.kakao.maps.Map(mapRef.current, options); // 맵 화면 띄울변수
                        mapInstanceRef.current = map;


                        // 마커 클러스터러를 생성합니다
                        // averageCenter: 클러스터에 포함된 마커들의 평균 위치를 클러스터 마커 위치로 설정
                        // minLevel: 클러스터를 표시할 최소 지도 레벨 (숫자가 클수록 더 많이 축소됐을 때 클러스터링)
                        // disableClickZoom: true로 설정하면 클러스터 클릭 시 자동 확대를 막고 직접 제어 가능
                        const clusterer = new window.kakao.maps.MarkerClusterer({
                            map: map,           // 마커들을 클러스터로 관리하고 표시할 지도 객체
                            averageCenter: true, // 클러스터에 포함된 마커들의 평균 위치를 클러스터 마커 위치로 설정
                            minLevel: 2,        // 클러스터 할 최소 지도 레벨
                            disableClickZoom: true, // 클러스터 마커를 클릭했을 때 지도가 자동 확대되지 않도록 설정
                            calculator: [10, 30, 50],
                        });
                        clustererRef.current = clusterer;

                        // 클러스터 마커를 클릭했을 때 해당 클러스터 중심을 기준으로 지도를 1레벨씩 확대합니다
                        window.kakao.maps.event.addListener(clusterer, 'clusterclick', function (cluster) {
                            // 현재 지도 레벨에서 1레벨 확대한 레벨
                            var level = map.getLevel() - 1;
                            // 클릭된 클러스터의 중심 위치를 기준으로 지도를 확대합니다
                            map.setLevel(level, { anchor: cluster.getCenter() });
                        });
                        console.log("요청 좌표", lat, lng);

                        Promise.all([   // // 초기 로드 - 전체 contents + 인증 가능 목록
                            fetch(`http://localhost:8080/api/safety/contents?lat=${lat}&lng=${lng}&radius=1000`).then(res => res.json()),
                            fetch(`http://localhost:8080/api/safety/auth-contents?lat=${currentlat}&lng=${currentlng}&radius=50`).then(res => res.json())
                        ]).then(([contents, auth]) => {
                            authContentsRef.current = auth;

                            // 그룹화 로직 적용
                            const grouped = groupNearbyContents(contents);
                            Object.values(grouped).forEach(group => {
                                createMarker(group, map, clusterer);
                            });
                        });

                        // 현재 위치 마커
                        const makerPosition = new window.kakao.maps.LatLng(lat, lng);
                        const UserImage = new window.kakao.maps.MarkerImage(peopleIcon, new window.kakao.maps.Size(27, 44));
                        const maker = new window.kakao.maps.Marker({ position: makerPosition, image: UserImage });
                        maker.setMap(map);

                        let clickMaker = null;
                        window.kakao.maps.event.addListener(map, 'click', async (mouseEvent) => {
                            const lat = mouseEvent.latLng.getLat();
                            const lng = mouseEvent.latLng.getLng();

                            // 1. 기존 마커 및 클러스터러 비우기
                            if (clickMaker) { clickMaker.setMap(null); }
                            clearMarkers(clusterer);

                            // 2. 클릭 지점 마커 표시
                            clickMaker = new window.kakao.maps.Marker({
                                position: new window.kakao.maps.LatLng(lat, lng),
                            });
                            clickMaker.setMap(map);

                            // 3. 공통 함수 호출
                            fetchAndRenderMarkers(lat, lng, map, clusterer);

                            // 4. 정주여건 점수 데이터 가져오기
                            const response = await fetch("http://localhost:8080/api/safety/", {
                                method: "POST",
                                headers: { "Content-Type": "application/json" },
                                body: JSON.stringify({ lat, lng, radius: 500 }),
                            });
                            const data = await response.json();

                            if (onScoreReady) onScoreReady(data);
                            console.log("결과", data);
                            console.log("클릭 위치 갱신 완료:", lat, lng);

                            if (onScoreReady) onScoreReady(data);    // 점수데이터가 있으면 전달
                        });
                    },
                    () => { // GPS 실패
                        const lat = 35.1798;
                        const lng = 128.1076;
                        const options = { center: new window.kakao.maps.LatLng(lat, lng), level: 3 };
                        const map = new window.kakao.maps.Map(mapRef.current, options);
                        mapInstanceRef.current = map;   // 저장

                        const clusterer = new window.kakao.maps.MarkerClusterer({
                            map: map,
                            averageCenter: true,
                            minLevel: 2,
                            disableClickZoom: true
                        });
                        clustererRef.current = clusterer;   // 저장

                        fetch(`http://localhost:8080/api/safety/contents?lat=${lat}&lng=${lng}&radius=1000`)
                            .then(res => res.json())
                            .then(contents => {
                                const grouped = groupNearbyContents(contents);
                                Object.values(grouped).forEach(group => {
                                    createMarker(group, map, clusterer);
                                });
                            });

                        const markerPosition = new window.kakao.maps.LatLng(lat, lng);
                        const marker = new window.kakao.maps.Marker({ position: markerPosition });
                        marker.setMap(map);

                        let clickMaker = null;
                        window.kakao.maps.event.addListener(map, 'click', async (mouseEvent) => {
                            const lat = mouseEvent.latLng.getLat();
                            const lng = mouseEvent.latLng.getLng();

                            // 1. 기존 마커 및 클러스터러 비우기
                            if (clickMaker) { clickMaker.setMap(null); }
                            clearMarkers(clusterer);

                            // 2. 클릭 지점 마커 표시
                            clickMaker = new window.kakao.maps.Marker({
                                position: new window.kakao.maps.LatLng(lat, lng),
                            });
                            clickMaker.setMap(map);

                            // 3. 공통 함수 호출
                            fetchAndRenderMarkers(lat, lng, map, clusterer);

                            // 4. 정주여건 점수 데이터 가져오기
                            const response = await fetch("http://localhost:8080/api/safety/", {
                                method: "POST",
                                headers: { "Content-Type": "application/json" },
                                body: JSON.stringify({ lat, lng, radius: 500 }),
                            });
                            const data = await response.json();
                            if (onScoreReady) onScoreReady(data);

                            console.log("클릭 위치 갱신 완료:", lat, lng);
                        });
                    }   // GPS실패 함수 끝
                );  // 위치함수 끝
            });     // window.kakao.maps.load(맵 띄우는 함수) 끝
        };      // script.onload함수 끝
        document.head.appendChild(script);
    }, [viewType]);     // useEffect함수 끝

    // 카테고리 변경 시 마커 업데이트 useEffect
    useEffect(() => {
        shopCategoryRef.current = shopCategory;
        contentCategoryRef.current = contentCategory;
        const map = mapInstanceRef.current;
        const clusterer = clustererRef.current;
        if (!map || !clusterer) return;

        clearMarkers(clusterer);    // 기존 마커 전부 제거

        const lat = map.getCenter().getLat();
        const lng = map.getCenter().getLng();
        fetchAndRenderMarkers(lat, lng, map, clusterer);

    }, [shopCategory, contentCategory]);

    // moveToLocation ref 업데이트
    useEffect(() => {
        moveToLocationRef.current = moveToLocation;
    }, [moveToLocation]);

    // interval로 지도 이동 감지
    useEffect(() => {
        const moveInterval = setInterval(() => {
            if (moveToLocationRef.current && mapInstanceRef.current) {
                const { lat, lng } = moveToLocationRef.current;
                const moveLatLng = new window.kakao.maps.LatLng(lat, lng);
                mapInstanceRef.current.setCenter(moveLatLng);
                mapInstanceRef.current.setLevel(3);
                moveToLocationRef.current = null;
            }
        }, 100);
        return () => clearInterval(moveInterval);
    }, []);

    return <div ref={mapRef} style={{ width: "100%", height: "100%", position: "absolute" }} />;
}

export default KakaoMap;
/*
1. useEffect, useRef 가져오고
2. mapRef(지도 div), mapInstanceRef(map 객체), clustererRef(클러스터러),
   contentMarkersRef(마커 배열), infowindowsRef(인포윈도우 배열), authContentsRef(인증 목록) 선언
3. getMarkerIcon() - 카테고리별 마커 아이콘 반환 함수
4. createMarker() - 마커 생성 + 인포윈도우 + 인증하기 버튼 공통 함수
5. clearMarkers() - 기존 마커/인포윈도우 전부 제거 공통 함수
6. script 만들고 API키 + clusterer 라이브러리 넣고
7. script가 다운로드 완료되면 카카오맵 초기화
8. GPS 실행
9. GPS 성공 → 진주시청 좌표로 지도 고정 (추후 position.coords로 변경 예정)
         → 클러스터러 생성
         → 초기 1km 반경 contents + 인증가능 목록(50m) 동시 호출
         → 현재 위치 마커 표시
         → 지도 클릭 시 클릭 위치 기준 1km 반경 contents 마커 업데이트
   GPS 실패 → 진주시청 좌표로 지도 띄움 (인증 기능 없음)
10. 카테고리 변경 useEffect - shopCategory, contentCategory 바뀌면
         → 기존 마커 제거 후 선택한 카테고리 기준으로 마커 재호출
11. script를 <head>에 넣어서 다운로드 시작
12. div를 화면에 그려줌 (return)
13. 다른 파일에서 쓸 수 있게 KakaoMap으로 내보냄

참고
1. 500m 반경의 안심등급
2. 1km 반경의 contents, shop 출력
3. 50m 반경의 인증 기능
4. 카테고리별 마커 아이콘: 관광/축제/문화재/체육/미술(노란색), 가맹점(파란색)
*/