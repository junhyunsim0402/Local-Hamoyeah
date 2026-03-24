package com.hamoyeah.safety.entity;

public enum AirStation {
    DAEAN("대안동", "경남 진주시 진주대로 1052", 35.193554523, 128.084507909),
    SANGDAE("상대동(진주)", "경남 진주시 동진로 279", 35.180838373, 128.121606519),
    SANGBONG("상봉동", "경상남도 진주시 북장대로64번길 14", 35.195891741, 128.074566516),
    JEONGCHON("정촌면", "경상남도 진주시 정촌면 예하리 1340", 35.12488670125278, 128.09976078189248);

    private final String name;
    private final String address;
    private final Double lat;
    private final Double lon;

    AirStation(String name, String address, Double lat, Double lon) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }
}
