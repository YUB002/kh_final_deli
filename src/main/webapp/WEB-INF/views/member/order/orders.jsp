
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Home</title>

    <!--jQuery-->
    <script src="https://code.jquery.com/jquery-3.6.1.min.js"
            integrity="sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=" crossorigin="anonymous">
    </script>

    <!--bootstrap-->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-iYQeCzEYFbKjA/T2uDLTpkwGzCiq6soy8tYaI1GyVh/UjpbCx/TYkiZhlZB6+fzT"
          crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-u1OknCvxWvY5kfmNBILK2hRnQC3Pr17a+RTT6rIHI7NnikvbZlHgTPOOmMi466C8"
            crossorigin="anonymous"></script>
    <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
    <script type="text/javascript" src="https://cdn.iamport.kr/js/iamport.payment-1.2.0.js"></script>

</head>
<style>
    /*main CSS*/
    .container {
        border: 1px solid black;
        width: 375px;
        height: 100%;
    }

    /*modal CSS*/
    #modal.modal-overlay {
        width: 100%;
        height: 100%;
        position: absolute;
        left: 0;
        top: 0;
        display: none;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background: rgba(255, 255, 255, 0.25);
        box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
        backdrop-filter: blur(1.5px);
        -webkit-backdrop-filter: blur(1.5px);
        border-radius: 10px;
        border: 1px solid rgba(255, 255, 255, 0.18);
    }
    #modal .modal-window {
        background: rgba( 69, 139, 197, 0.70 );
        box-shadow: 0 8px 32px 0 rgba( 31, 38, 135, 0.37 );
        backdrop-filter: blur( 13.5px );
        -webkit-backdrop-filter: blur( 13.5px );
        border-radius: 10px;
        border: 1px solid rgba( 255, 255, 255, 0.18 );
        width: 400px;
        height: 500px;
        position: relative;
        top: -100px;
        padding: 10px;
    }
    #modal .title {
        padding-left: 10px;
        display: inline;
        text-shadow: 1px 1px 2px gray;
        color: white;

    }
    #modal .title h2 {
        display: inline;
    }
    #modal .close-area {
        display: inline;
        float: right;
        padding-right: 10px;
        cursor: pointer;
        text-shadow: 1px 1px 2px gray;
        color: white;
    }

    #modal .content {
        margin-top: 20px;
        padding: 0px 10px;
        text-shadow: 1px 1px 2px gray;
        color: white;
    }
</style>
<body>

<div class="container">
    <h2>배달 정보</h2>
    <hr>
    <div id="mainAddress">address1출력
        <a href="memberSignUp.jsp"><button type="button" id="changeAddress">변경</button></a>
        <%--        모달로 주소 변경 구현 ( 아래 내용이 들어감 )--%>
        <button id="btn_modal">주소 변경</button>
        <div id="modal" class="modal-overlay">
            <div class="modal-window">
                <div class="title">
                    <h2>주소 변경</h2>
                </div>
                <div class="close-area">X</div>
                <div class="content">
                    <input type="text" id="postcode" placeholder="우편번호">
                    <input type="button" onclick="postcode()" value="찾기" id="btnSearch"><br>
                    <input type="text" id="address1" placeholder="지번주소">
                    <input type="text" id="address2" placeholder="상세주소">
                    <p>주소 변경</p>
                    <p>zipcode</p>
                    <p>address1</p>
                    <p>address2</p>
                </div>
            </div>
        </div>

    </div>
    <input type="text" placeholder="Address2">
    <div>핸드폰 번호
        <button type="button">변경</button>
    </div>
    <hr>
    <div>요청사항</div>
    <div>
        체크박스 수저 포크 / 단무지
    </div>
    <input type="text" placeholder="사장님한테 전달할 말">
    <input type="text" placeholder="라이더님한테 전달할 말">
    <hr>
    <div>결제수단
        <select>
            <option>선택</option>
            <option>카카오페이</option>
            <option>신용/체크카드</option>
            <option>현금</option>
        </select>
    </div>
    <hr>
    <div>
        <button>할인쿠폰</button>
        <div>할인쿠폰 출력</div>
    </div>
    <div>포인트
        <div>보유 포인트 출력
            <input type="text" placeholder="사용할 포인트">
        </div>
        <hr>
        <div>총 결제 금액
            <div>주문 내역 출력</div>
        </div>
        <div>쿠폰할인
            <input type="text" placeholder="쿠폰 할인 출력">
        </div>
        <div>포인트 할인
            <input type="text" placeholder="포인트 할인 출력">
        </div>
        <div>배달팁
            <input type="text" placeholder="사용할 포인트 출력">
        </div>
        <hr>
        <button onclick="requestPay()">카카오 페이 결제</button>
        <button type="button" class="btn_payment">다른 결제 수단으로 결제</button>
    </div>

</div>

<script>
    // 버튼 클릭 시 모달창 오픈
    const modal = document.getElementById("modal")
    const btnModal = document.getElementById("btn_modal")
    btnModal.addEventListener("click", e => {
        modal.style.display = "flex"
    })
    // 모달창 클로즈(X) 버튼 누를시 모달 종료
    const closeBtn = modal.querySelector(".close-area")
    closeBtn.addEventListener("click",e =>{
        modal.style.display ="none"
    })

    // 모달창 바깥 영역 클릭 시 모달 종료
    modal.addEventListener("click",e =>{
        const evTarget = e.target
        if(evTarget.classList.contains("modal-overlay")){
            modal.style.display = "none"
        }
    })

    // 모달창 켜진 상태에서 ESC 버튼 클릭시 모달 종료
    window.addEventListener("keyup", e => {
        if(modal.style.display === "flex" && e.key === "Escape") {
            modal.style.display = "none"
        }
    })


    document.getElementById("btnSearch").onclick = function () {
        new daum.Postcode({
            oncomplete: function (data) {
                document.getElementById('postcode').value = data.zonecode;
                document.getElementById("address1").value = data.jibunAddress; // data.roadAddress
            }
        }).open();
    }

    $(".btn_payment").click(function() {
        //class가 btn_payment인 태그를 선택했을 때 작동한다.
        IMP.init("imp52685667");
        //결제시 전달되는 정보
        IMP.request_pay({
            pg: 'html5_inicis',
            pay_method: 'card',
            merchant_uid: 'merchant_' + new Date().getTime(),
            name: '결제테스트',// 상품명,
            amount: 100,//상품 가격,
            buyer_email: 'iamport@siot.do',//구매자 이메일,
            buyer_name: '구매자이름',
            buyer_tel: '010-1234-5678',//구매자 연락처
            buyer_addr: '서울특별시 강남구 삼성동',//구매자 주소
            buyer_postcode: '123-456'//구매자 우편번호
        }, function (rsp) {
            var result = '';
            if (rsp.success) {
                var msg = '결제가 완료되었습니다.';
                msg += '고유ID : ' + rsp.imp_uid;
                msg += '상점 거래ID : ' + rsp.merchant_uid;
                msg += '결제 금액 : ' + rsp.paid_amount;
                msg += '카드 승인번호 : ' + rsp.apply_num;
                result = '0';
            } else {
                var msg = '결제에 실패하였습니다.';
                msg += '에러내용 : ' + rsp.error_msg;
                result = '1';
            }
            if (result == '0') {
                location.href = $.getContextPath() + "/main/success";
            }
            alert(msg);
        });
    });



    var IMP = window.IMP;
    IMP.init("imp52685667");
    function requestPay() {
        IMP.request_pay({
            pg: 'kakaopay',
            pay_method: 'card',
            merchant_uid: 'merchant_' + new Date().getTime(),
            name: '결제',
            amount: 64900,//'가격 입력',
            buyer_email: 'jaeyoung1870@gmail.com',//'구매자 이메일',
            buyer_name: '허재영',//'구매자 이름'
            buyer_tel: '010-4242-4242',
            buyer_addr: '서울특별시 강남구 신사동',
            buyer_postcode: '01181'
        }, function (rsp) {
            if (rsp.success) {
                var msg = '결제가 완료되었습니다.';
                //window.parent.location.href = "/payComplete.paymem?payGoodsSeq=" + goodsSeq + "&payMemberEmail=" + userEmail;
            } else {
                var msg = '결제에 실패하였습니다.';
                rsp.error_msg;

            }
        });
    }

</script>

</body>
</html>