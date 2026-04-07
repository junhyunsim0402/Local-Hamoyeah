import React, { useState } from 'react';
import './Login.css';
// 이미지들을 빌드 환경에 맞게 import 합니다.
import topBg from '../assets/top_bg.png';
import bottomBg from '../assets/bottom_bg.png';
import logo from '../assets/logo.png';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
function LoginPage() {
  const [loginData, setLoginData]=useState({
    id:'',
    password:''
  });

  const [id, setId] = useState('');
  const [password, setPassword] = useState('');

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try{
      const obj={email: id, password: password};
      const response = await axios.post("http://localhost:8080/user/login", obj);
    if (response.data) { 
      localStorage.setItem("token", response.data);
      alert("로그인 성공");
      navigate("/main");
    } 
  } catch(error){
      const errorMsg = error.response?.data || "로그인 실패";
      alert(errorMsg);
    }
};

  return (
    <div className="login-page">
      {/* 1. 배경 이미지 레이어 (z-index: 1) */}
      <img src={topBg} alt="" className="login-bg-image login-top-bg" />
      <img src={bottomBg} alt="" className="login-bg-image login-bottom-bg" />

      {/* 2. 실제 콘텐츠 레이어 (z-index: 10) */}
      <div className="login-content">
        {/* 상단 배경 위에 로고 배치 */}
        <div className="logo-section">
          <img src={logo} alt="Project Logo" className="login-logo" />
        </div>

        {/* 중앙: 입력창 + 로그인 버튼 (flex) */}
        <form onSubmit={handleLogin} className="login-form">
          <div className="input-group">
            <input 
              type="text" 
              placeholder="이메일" 
              value={id}
              onChange={(e) => setId(e.target.value)}
              className="login-input"
            />
            <input 
              type="password" 
              placeholder="비밀번호" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="login-input"
            />
          </div>
          
          {/* 입력창 오른쪽에 로그인 버튼 */}
          <button type="submit" className="login-submit-btn">로그인</button>
        </form>

        {/* 하단 여백 공간 확보 */}
        <div className="spacer"></div>
      </div>

      {/* 3. 최하단: 회원가입 이동 버튼 (화면에 고정) */}
      <footer className="login-footer">
        <button className="join-link-btn" onClick={() => navigate('/join')}>
        회원가입
        </button>
      </footer>
    </div>
  );
}

export default LoginPage;