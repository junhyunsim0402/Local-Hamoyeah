import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './RecommendBanner.css';

function RecommendBanner({ onContentClick }) {
    const [top2, setTop2] = useState([]);
    const getImageUrl = (imgUrl) => {
        if (!imgUrl) return null;
        const urls = imgUrl.replace(/[\[\]]/g, '').split(',');
        return urls[0].trim();
    };


    useEffect(() => {
        axios.get('http://localhost:8080/api/recommend/top2')
            .then(res => {
                console.log('추천 데이터:', res.data);
                setTop2(res.data);
            })
            .catch(e => console.error(e));
    }, []);

    if (top2.length === 0) return null;

    return (
        <div className="recommend-banner">
            <div className="recommend-title">오늘의 진주 추천</div>
            <div className="recommend-list">
                {top2.map((item, index) => (
                    <div key={index} className="recommend-item" onClick={() => onContentClick(item)}>
                        <img src={getImageUrl(item.imgUrl)} alt={item.contentTitle} className="recommend-img" />
                        <div className="recommend-name">{item.contentTitle}</div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default RecommendBanner;