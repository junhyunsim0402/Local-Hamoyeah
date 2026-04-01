import React, { useState } from 'react';
import './AuthModal.css';

function AuthModal({ isOpen, onClose, targetTitle }) {
    const [preview, setPreview] = useState(null); // 이미지 미리보기 주소
    const [uploadFile, setUploadFile] = useState(null); // 실제 서버에 보낼 파일

    if (!isOpen) return null;

    // 파일 선택 시 실행되는 함수
    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setUploadFile(file);
            // 브라우저에서 임시로 볼 수 있는 URL 생성
            setPreview(URL.createObjectURL(file));
        }
    };

    const handleSubmit = () => {
        if (!uploadFile) {
            alert("인증샷을 찍거나 선택해 주세요! 📸");
            return;
        }
        // TODO: 여기서 백엔드로 전송하는 로직 (Axios 등)
        console.log("제출 파일:", uploadFile);
        alert(`${targetTitle} 인증샷이 제출되었습니다! 관리자 승인을 기다려주세요.`);
        onClose();
    };

    return (
        <div className="auth-modal-overlay" onClick={onClose}>
            <div className="auth-modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="auth-modal-close" onClick={onClose}>&times;</button>
                
                <h2 className="auth-modal-title">📸 인증샷 올리기</h2>
                <p className="auth-modal-subtitle">[{targetTitle}] </p>

                <div className="auth-upload-section">
                    <label htmlFor="auth-file-input" className="auth-drop-zone">
                        {preview ? (
                            <img src={preview} alt="미리보기" className="auth-preview-img" />
                        ) : (
                            <div className="auth-placeholder">
                                <span className="auth-plus-icon">+</span>
                                <p>사진 찍기 또는 선택</p>
                            </div>
                        )}
                        <input 
                            type="file" 
                            id="auth-file-input" 
                            accept="image/*" 
                            capture="environment" // 모바일에서 바로 카메라 띄우기 ⭐
                            onChange={handleFileChange} 
                            style={{ display: 'none' }}
                        />
                    </label>
                </div>

                <div className="auth-modal-footer">
                    <button className="auth-cancel-btn" onClick={onClose}>취소</button>
                    <button className="auth-submit-btn" onClick={handleSubmit}>제출하기</button>
                </div>
            </div>
        </div>
    );
}

export default AuthModal;