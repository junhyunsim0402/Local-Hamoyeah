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
    confirmPassword: '',
    isAdmin: false
  });

  const [isEmailVerified, setIsEmailVerified] = useState(false);
  const navigate = useNavigate();

  // 이메일 인증 번호 발송 함수
  const sendVerificationEmail = async () => {
    const targetEmail = formData.id; 
    if (!targetEmail) {
      alert("이메일을 입력해주세요.");
      return;
    }
    try {
      const response = await axios.post(`http://localhost:8080/email/send?email=${targetEmail}`);
      alert(response.data.message || "인증번호가 발송되었습니다.");
    } catch (error) {
      console.error("발송 에러:", error);
      alert(error.response?.data?.message || "인증번호 발송에 실패했습니다.");
    }
  };

  // 인증번호 확인 함수
  const checkVerificationCode = async () => {
    if (!formData.id || !formData.emailVerify) {
      alert("이메일과 인증번호를 모두 입력해주세요.");
      return;
    }
    try {
      const response = await axios.post(`http://localhost:8080/email/verify?email=${formData.id}&code=${formData.emailVerify}`);

      if (response.data === true || response.data.success === true) { 
        alert("인증에 성공했습니다.");
        setIsEmailVerified(true);
      } else {
        alert("인증번호가 일치하지 않습니다.");
        setIsEmailVerified(false);
      }
    } catch (error) {
      console.error("인증 확인 에러:", error);
      alert("인증번호가 틀렸거나 확인 중 오류가 발생했습니다.");
      setIsEmailVerified(false);
    }
  };

  // 입력한 값 대입 함수
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  // 회원가입 함수
  const handleJoin = async (e) => {
    e.preventDefault();
    if (!isEmailVerified) {
      alert("이메일 인증을 먼저 완료해주세요.");
      return;
    }
    if (!formData.password || formData.password !== formData.confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }
    try {
      const { name, id, password } = formData;
      const obj = {email: id, password: password, nickname: name};
      const response = await axios.post("http://localhost:8080/user/signup", obj);
      if (response.data === true) { 
        alert("회원가입 성공!");
        navigate("/login");
      } else {
        alert("회원가입 실패: 이미 존재하는 계정이거나 정보가 잘못되었습니다.");
      }
    } catch (error) {
      console.error("서버 에러 상세:", error.response?.data);
      const errorMsg = error.response?.data?.message || "서버 통신 중 오류가 발생했습니다.";
      alert(errorMsg);
    }
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