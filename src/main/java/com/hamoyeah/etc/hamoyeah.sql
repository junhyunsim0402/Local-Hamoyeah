drop database if exists hamoyeah;
create database hamoyeah;
use hamoyeah;
CREATE TABLE noise (
    noise_id INT AUTO_INCREMENT PRIMARY KEY,
    day_avg DECIMAL(4,1) NOT NULL,
    night_avg DECIMAL(4,1) NOT NULL,
    location_name VARCHAR(100),
    area_type VARCHAR(20)
);
CREATE TABLE streetlamp (
    lamp_id INT AUTO_INCREMENT PRIMARY KEY,
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    address VARCHAR(255) NOT NULL
);
CREATE TABLE cctv (
    cctv_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    install_cnt INT NOT NULL
);
CREATE TABLE airpollution (
    air_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    air_area VARCHAR(20) NOT NULL,
    measured_at DATETIME NOT NULL,
    pm10_value INT NOT NULL,
    pm25_value INT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL
);
CREATE TABLE category (
    content_type INT,
    content_category INT,
    PRIMARY KEY (content_type, content_category)
);
CREATE TABLE contents (
    content_id INT AUTO_INCREMENT PRIMARY KEY,
    content_type INT NOT NULL,
    content_category INT NOT NULL,
    content_title VARCHAR(100) NOT NULL,
    content_des TEXT,
    start_date DATE,
    end_date DATE,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,

    CONSTRAINT fk_category
	FOREIGN KEY (content_type, content_category)
	REFERENCES category(content_type, content_category)
);
CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    total_points INT NOT NULL DEFAULT 0,
    isAdmin BOOLEAN NOT NULL DEFAULT FALSE
);
INSERT INTO category (content_type, content_category) VALUES
(1,1), -- 콘텐츠 - 관광지
(1,2), -- 콘텐츠 - 축제
(1,3), -- 콘텐츠 - 문화재
(2,1), -- 운동시설
(3,1), -- 미술 - 공공미술
(3,2); -- 미술 - 건축물미술
INSERT INTO contents 
(content_type, content_category, content_title, content_des, start_date, end_date, address, latitude, longitude) VALUES
(1,1, '진주성', '관광지', NULL, NULL, '진주시 본성동', 35.1895, 128.0802),
(1,2, '남강유등축제', '축제', '2026-10-01', '2026-10-15', '진주시 남강', 35.1920, 128.0840),
(1,3, '촉석루', '문화재', NULL, NULL, '진주시 남성동', 35.1890, 128.0810),
(2,1, '진주 체육관', '운동시설', NULL, NULL, '진주시 초전동', 35.2100, 128.1000),
(3,1, '벽화거리', '공공미술', NULL, NULL, '진주시 평거동', 35.1800, 128.0700),
(3,2, '건축 미술관', '건축물 미술', NULL, NULL, '진주시 강남동', 35.1900, 128.0850),
(3,2, '조형물', '건축물 미술', NULL, NULL, '진주시 하대동', 35.1950, 128.0900),
(1,1, '진양호', '관광지', NULL, NULL, '진주시 판문동', 35.1650, 128.0400),
(2,1, '헬스장', '운동시설', NULL, NULL, '진주시 충무공동', 35.2050, 128.1250),
(3,1, '공공미술 작품', '미술', NULL, NULL, '진주시 상대동', 35.2000, 128.0950);
INSERT INTO airpollution 
(air_area, measured_at, pm10_value, pm25_value, latitude, longitude) VALUES
('대안동', '2026-03-01 09:00:00', 45, 22, 35.1901, 128.0801),
('상대동', '2026-03-01 10:00:00', 50, 25, 35.2002, 128.0902),
('상봉동', '2026-03-01 11:00:00', 55, 28, 35.2103, 128.1003),
('정촌면', '2026-03-01 12:00:00', 40, 18, 35.1704, 128.0604),
('대안동', '2026-03-02 09:00:00', 48, 21, 35.1905, 128.0805),
('상대동', '2026-03-02 10:00:00', 52, 26, 35.2006, 128.0906),
('상봉동', '2026-03-02 11:00:00', 60, 30, 35.2107, 128.1007),
('정촌면', '2026-03-02 12:00:00', 42, 19, 35.1708, 128.0608),
('대안동', '2026-03-03 09:00:00', 47, 23, 35.1909, 128.0809),
('상대동', '2026-03-03 10:00:00', 53, 27, 35.2010, 128.0910);
INSERT INTO noise 
(day_avg, night_avg, location_name, area_type) VALUES
(55.5, 45.2, '진주역', '일반'),
(60.1, 50.3, '고속버스터미널', '도로변'),
(48.2, 38.4, '진주성', '일반'),
(65.0, 55.0, '중앙시장', '도로변'),
(52.3, 42.1, '경상대', '일반'),
(58.7, 47.9, '하대동', '도로변'),
(49.5, 39.8, '평거동', '일반'),
(62.4, 53.2, '충무공동', '도로변'),
(54.0, 44.5, '상대동', '일반'),
(59.9, 49.0, '상봉동', '도로변');
INSERT INTO streetlamp 
(latitude, longitude, address) VALUES
(35.1901, 128.0801, '진주시 본성동 1'),
(35.1910, 128.0810, '진주시 본성동 2'),
(35.1920, 128.0820, '진주시 남성동 1'),
(35.1930, 128.0830, '진주시 남성동 2'),
(35.1940, 128.0840, '진주시 강남동 1'),
(35.1950, 128.0850, '진주시 강남동 2'),
(35.1960, 128.0860, '진주시 하대동 1'),
(35.1970, 128.0870, '진주시 하대동 2'),
(35.1980, 128.0880, '진주시 상대동 1'),
(35.1990, 128.0890, '진주시 상대동 2');
INSERT INTO cctv 
(latitude, longitude, install_cnt) VALUES
(35.1901, 128.0801, 2),
(35.1911, 128.0811, 3),
(35.1922, 128.0822, 1),
(35.1933, 128.0833, 4),
(35.1944, 128.0844, 2),
(35.1955, 128.0855, 5),
(35.1966, 128.0866, 1),
(35.1977, 128.0877, 3),
(35.1988, 128.0888, 2),
(35.1999, 128.0899, 4);
INSERT INTO user 
(email, password, nickname, total_points, isAdmin) VALUES
('user1@test.com', '1234', '유저1', 0, FALSE),
('user2@test.com', '1234', '유저2', 10, FALSE),
('user3@test.com', '1234', '유저3', 20, FALSE),
('user4@test.com', '1234', '유저4', 30, FALSE),
('user5@test.com', '1234', '유저5', 40, FALSE),
('user6@test.com', '1234', '유저6', 50, FALSE),
('user7@test.com', '1234', '유저7', 60, FALSE),
('user8@test.com', '1234', '유저8', 70, FALSE),
('user9@test.com', '1234', '유저9', 80, FALSE),
('admin@test.com', 'admin', '관리자', 999, TRUE);