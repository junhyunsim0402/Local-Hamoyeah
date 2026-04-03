import React, { useState } from 'react';
import './Join.css';
import topBg from '../assets/top_bg.png';
import bottomBg from '../assets/bottom_bg.png';
import logo from '../assets/logo.png';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
function JoinPage() {
  const [formData, setFormData] = useState({
    name: '',
    id: '', // 이메일
    emailVerify: '', // 인증번호
    password: '',
    confirmPassword: ''
  });
  const navigate = useNavigate();

  // 이메일 인증번호 발송 함수
  const sendVerificationEmail = async () => {
    console.log(email);
    try{
      const response=await axios.post(`http://localhost:8080/email/send?email=${formData.email}`);
      console.log(response.data.message);
      alert(response.data.message);
    } catch(error){
      console.error("에러 발생", error);
      alert("이메일 전송 오류")
    }
  };

  // 인증번호 확인 함수
  const checkVerificationCode = async () => {
    console.log(`인증번호 ${formData.emailVerify} 확인 시도`);
    alert("인증되었습니다.");
  };
  const handleChange = async (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleJoin = async (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      alert("비밀번호가 일치하지 않아요!");
      return;
    }
    try{
      const{userId,password,nickname}=formData;
      const obj={userId:userId, password:password, nickname:nickname}
      const response=await axios.post("http://localhost:8080/user/signup",obj);
      const data=response.data;
      if(data==true){
      alert("회원가입 성공");
      navigate("/login");
    } else{alert("회원가입 실패");}
    } catch(error){
      console.error("에러 발생", error);}

    
  };

  return (
    <div className="join-page">
      <img src={topBg} alt="" className="join-bg-image join-top-bg" />
      <img src={bottomBg} alt="" className="join-bg-image join-bottom-bg" />

      <div className="join-content">
        <div className="logo-section">
          <img src={logo} alt="Logo" className="join-logo" />
        </div>

        <form onSubmit={handleJoin} className="join-form">
          <div className="join-input-group">
            <input 
              name="name"
              type="text" 
              placeholder="닉네임" 
              value={formData.name}
              onChange={handleChange}
              className="join-input"
            />
            <div className="input-with-button">
              <input 
                name="id"
                type="text" 
                placeholder="이메일" 
                value={formData.id}
                onChange={handleChange}
                className="join-input"
              />
              <button type="button" onClick={sendVerificationEmail} className="inner-btn">인증</button>
            </div>
            <input 
              name="emailVerify"
              type="text" 
              placeholder="이메일 인증번호" 
              value={formData.emailVerify}
              onChange={handleChange}
              className="join-input"
            />
            
            {/* 인증번호 입력창 바로 아래 확인 버튼 */}
            <button type="button" onClick={checkVerificationCode} className="verify-check-btn">인증하기</button>

            <input 
              name="password"
              type="password" 
              placeholder="비밀번호" 
              value={formData.password}
              onChange={handleChange}
              className="join-input"
            />
            <input 
              name="confirmPassword"
              type="password" 
              placeholder="비밀번호 확인" 
              value={formData.confirmPassword}
              onChange={handleChange}
              className="join-input"
            />
          </div>
          <button type="submit" className="join-submit-btn">가입하기</button>
        </form>
      </div>

      <footer className="join-footer">
      <button className="back-login-btn" onClick={() => navigate('/login')}>
        이미 계정이 있나요? <strong>로그인</strong>
      </button>
    </footer>
    </div>
  );
}

export default JoinPage;