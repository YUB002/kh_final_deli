
let lang_kor = {
    "decimal" : "",
    "emptyTable" : "데이터가 없습니다.",
    "info" : "_START_ - _END_ (총 _TOTAL_ 개)",
    "infoEmpty" : "0 개",
    "infoFiltered" : "(전체 _MAX_ 개 중 검색결과)",
    "infoPostFix" : "",
    "thousands" : ",",
    "lengthMenu" : "_MENU_ 개씩 보기",
    "loadingRecords" : "로딩중...",
    "processing" : "처리중...",
    "search" : "검색 : ",
    "zeroRecords" : "검색된 데이터가 없습니다.",
    "paginate" : {
        "first" : "첫 페이지",
        "last" : "마지막 페이지",
        "next" : "다음",
        "previous" : "이전"
    },
    "aria" : {
        "sortAscending" : " :  오름차순 정렬",
        "sortDescending" : " :  내림차순 정렬"
    }
};

$(document).ready(function() {
    $('#myTable').DataTable( {
        language : lang_kor,
        order: [[0, 'desc']],lengthMenu: [
            [10, 25, 50, -1],
            [10, 25, 50, 'All'],
        ]
    } );
} );


$("#optionBtn").on("click", function () {
    let store_seq = $(this).siblings($(".storeOption")).val();
    let startDate = $("#startDate").val();
    let endDate = $("#endDate").val();

    startDate=startDate.replace(/-/g, '');
    endDate=endDate.replace(/-/g, '');

    console.log("시작 시간 스트링"+startDate+" ; "+endDate);
    $.ajax({
        url:"/owner/selectSales",
        type: "post",
        data: {
            store_seq:store_seq,
            startDate:startDate,
            endDate:endDate
        },
        dataType:"json"
    }).done(function (data){
            $(".storeSales").empty();
            let r='';
            for (i = 0; i < data.length; i++) {
                r+="<tr>";
                r+="<td>"+ data[i].daily_date+"</td>";
                r+="<td>"+data[i].daily_sales.toLocaleString('ko-KR')+"</td>";
                r+="<td class='orderCnt'>"+data[i].daily_order_cnt+"</td>";
                r+="</tr>";
            }
            $(".storeSales").append(r);
    })

})

let now_utc = Date.now()
let timeOff = new Date().getTimezoneOffset()*60000;
let today = new Date(now_utc-timeOff).toISOString().substring(0, 16);

$("#startDate").on("change",function (){
    let startDateRange=$("#startDate").val();

    $("#startDate").attr("max",today);
    if(startDateRange > today){
        alert("오늘 이전으로 ㄱㄱ");
        $("#startDate").val("");
    }
})


$("#endDate").on("change",function (){
    let endDateRange=$("#endDate").val();
    let startDateRange=$("#startDate").val();

    $("#endDate").attr("min",startDateRange);
    if(endDateRange < startDateRange){
        alert("더 적은 날짜로 ㄱㄱ");
        $("#endDate").val("");
    }

    $("#endDate").attr("max",today);
    if(endDateRange > today){
        alert("오늘 이 ㄱㄱ");
        $("#endDate").val("");
    }
})

$(document).ready(function () {
    document.getElementById('startDate').value = new Date().toISOString().substring(0, 10);
    document.getElementById('endDate').value = new Date().toISOString().substring(0, 10);
})



$("#orderCtnBtn").on("click",function (){
    let storeSeq=$(this).prev().val();

    console.log("식당시퀀스"+storeSeq);

    $.ajax({
        url:"/owner/selectOrderCnt",
        type: "post",
        data: {
            storeSeq:storeSeq
        },
        dataType:"json"
    }).done(function (data){
        $("#memo").empty();
        $("#memo").append(
            $("<span>어제의 주문건수는 <span class='font'>"+data.daily_order_cnt +"</span> 건 입니다.</span>")
        );
        $("#memo").append($("<br>"));
        $("#memo").append(
            $("<span>오늘의 주문건수는 <span class='font'>"+data.td_order_cnt +"</span> 건 입니다.</span>")
        );
    })
})




