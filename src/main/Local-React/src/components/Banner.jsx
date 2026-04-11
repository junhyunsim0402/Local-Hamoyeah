import './Banner.css';
import banner_model from '../assets/banner_model.png';

function Banner() {
  return (
    <div className="banner-page">
      <div className="bg-circle circle-1"></div>
      <div className="bg-circle circle-2"></div>

      <div className="banner-content">
        <div className="banner-model">
          <img src={banner_model} alt="model" className="model"/>
          <div className="model-shadow"></div>
        </div>
        
        <h1 className="banner-title">
          하모예와 진주<br/>
          <p>한 바퀴 돌자!</p>
        </h1>
        
        <button className="banner-button">
          지금 진주 여행 시작하기!
        </button>
      </div>
    </div>
  );
}

export default Banner;