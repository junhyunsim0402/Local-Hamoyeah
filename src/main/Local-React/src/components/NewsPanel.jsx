import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './NewsPanel.css';

function NewsPanel({ newsCategory }) {
    const [newsList, setNewsList] = useState([]);
    const [loading, setLoading] = useState(false);
    useEffect(() => {
        const fetchNews = async () => {
            setLoading(true);
            try {
                const response = await axios.get('http://localhost:8080/api/news', {
                    params: {
                        contentType: newsCategory.contentType,
                        contentCategory: newsCategory.contentCategory
                    }
                });
                setNewsList(response.data);
            } catch (e) {
                console.error(e);
            } finally {
                setLoading(false);
            }
        };
        fetchNews();
    }, [newsCategory]);

    if (loading) return <div className="news-loading">뉴스 불러오는 중...</div>;

    return (
        <div className="news-panel">
            {newsList.length === 0 ? (
                <div className="news-empty">관련 뉴스가 없습니다.</div>
            ) : (
                newsList.map((news, index) => (
                    <a
                        key={index}
                        href={news.url}
                        target="_blank"
                        rel="noreferrer"
                        className="news-item"
                    >
                        <div className="news-title">{news.title}</div>
                        <div className="news-date">{news.date}</div>
                    </a>
                ))
            )}
        </div>
    );
}

export default NewsPanel;