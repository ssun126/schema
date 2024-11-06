
document.addEventListener("DOMContentLoaded", function() {
    //모달 Draggable 처리
    const modalHeader = document.querySelector(".dialog_head");
    const modalDialog = document.querySelector(".cui_dialog");

    // modalHeader가 null인지 확인하고, 존재하지 않으면 경고 메시지 출력
    if (!modalHeader || !modalDialog) {
        return; // 요소가 없으면 더 이상 코드를 실행하지 않음
    }

    let isDragging = false;
    let mouseOffset = { x: 0, y: 0 };
    let dialogOffset = { left: 10, top: 10 }; // 'right' -> 'top'으로 수정

    // 드래그 시작 시 모달의 현재 위치를 계산하여 저장
    modalHeader.addEventListener("mousedown", function (event) {
      isDragging = true;
      mouseOffset = { x: event.clientX, y: event.clientY };
      console.log("modalDialog.style.left========"+modalDialog.style.left);
      // 모달의 현재 위치가 없을 경우 기본값을 0으로 설정
      dialogOffset = {
        left: modalDialog.style.left === '' ? -50 : Number(modalDialog.style.left.replace('px', '')),
        right: modalDialog.style.top === '' ? -50 : Number(modalDialog.style.top.replace('px', ''))
      }
    });
    // 드래그 중일 때 모달 위치 변경
    document.addEventListener("mousemove", function (event) {
      if (!isDragging) {
        return;
      }
      // 새로운 위치 계산
      let newX = event.clientX - mouseOffset.x;
      let newY = event.clientY - mouseOffset.y;
      // 모달의 위치 업데이트
      modalDialog.style.left = `${dialogOffset.left + newX}px`
      modalDialog.style.top = `${dialogOffset.right + newY}px`
    });
     // 드래그 종료 시 이벤트 리스너 제거
    document.addEventListener("mouseup", function () {
      isDragging = false;
    });
});

//모달 초기화
function modal_init(id, status){
    // form 전체 초기화
    $("form[name='form"+id+"']").each(function() {
        this.reset();
        $("input[type=hidden]").val(''); //reset만으로 hidden type은 리셋이 안되기 때문에 써줌
    });
}

//모달 열기
function modal_open(id, status, sUrl){
  if(status !='add'){
   var sUrl = sUrl;

    $.ajax({
      type: "post",
      url:   sUrl,
      data: {
          "status": status
      },
      success: function(res) {
          console.log("요청성공", res);

          if (res.status == "ok") {
            $("#" + id).fadeIn();
            modal_init(status);

          }
      },
      error: function(err) {
          console.log("에러발생", err);
      }
    });
  }else{
    modal_init(id, status);
    $("#" + id).fadeIn();
  }
}

//모달 닫기
function modal_close(id){
    $("#" + id).fadeOut();
}
