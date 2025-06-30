<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
    <title>Air_K-Travel</title>
    <link rel="icon" type="image/png" sizes="32x32" href="./ico/favicon-32x32.png">
    <link rel="stylesheet" type="text/css" href="./css/main.css">
    <link rel="stylesheet" type="text/css" href="./css/main_page.css">
</head>
<body>
 <header class="headercss">
        <nav class="navcss">
            <ol>
                <li>
                    <span class="logos">Air_K-Travel</span>
                </li>
                <li>항공예약</li>
                <li>이벤트</li>
                <li>서비스 안내</li>
                <li>공지사항</li>
                <li>고객센터</li>
                <li><img src="./image/menu.svg" class="menu_ico"></li>
            </ol>
        </nav>
    </header>
    <main>
        <section class="main_view">
        <aside class="titles">Air<em class="color1">_</em>K-Travel (항공편 조회)</aside>
        <section class="air_search">
            <div class="air_parts">
                <ul class="air_pt1">
                    <li><label class="rd"><input type="radio" name="a" class="radio_box" checked> 왕복</label></li>
                    <li><label class="rd"><input type="radio" name="a" class="radio_box"> 편도</label></li>
                </ul>
                <ul class="air_pt2">
                    <li>
                    <span>출발지</span>
                    <span>
                        <select class="separts">
                            <option>출발지를 선택하세요</option>
                        </select>
                    </span>
                    </li>
                    <li><img src="./image/arrow.svg"></li>
                    <li>
                    <span>목적지</span>
                    <span>
                        <select class="separts">
                            <option>도착지를 선택하세요</option>
                        </select>
                    </span>
                    </li>
                </ul>
                <ul class="air_pt2">
                    <li>
                    <span>가는날</span>
                    <span>
                        <input type="date" class="datecss">
                    </span>
                    </li>
                    <li><img src="./image/arrow.svg"></li>
                    <li>
                    <span>오는날</span>
                    <span>
                        <input type="date" class="datecss">
                    </span>
                    </li>
                </ul>
                <ul class="air_pt2">
                    <li>
                    <span>좌석선택</span>
                    <span>
                        <select class="separts">
                            <option>좌석을 선택하세요</option>
                        </select>
                    </span>
                    </li>
                    <li></li>
                    <li>
                    <span>프로모션 코드</span>
                    <span>
                        <input type="text" class="datecss" placeholder="프로모션 코드가 있을 경우 입력하세요" maxlength="12">
                    </span>
                    </li>
                </ul>
                <div class="part_titles">인원 선택</div>
                <ul class="air_pt3">
                    <li>
                        성인 : <input type="text" class="persons" maxlength="3">
                    </li>
                    <li>
                        소아 : <input type="text" class="persons" maxlength="3">
                    </li>
                    <li>
                        유아 : <input type="text" class="persons" maxlength="3">
                    </li>
                    <li>
                        총인원 : <em class="totals">0</em>명
                    </li>
                </ul>
                <div class="btn">항공편 조회</div>
            </div>
        </section>
    </main>
</body>
</html>