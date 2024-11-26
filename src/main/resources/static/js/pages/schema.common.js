/*SCHEMA 공통 스크립트*/
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
    let dialogOffset = { left: 0, top: 0 }; // 'right' -> 'top'으로 수정

    // 드래그 시작 시 모달의 현재 위치를 계산하여 저장
    modalHeader.addEventListener("mousedown", function (event) {
      isDragging = true;
      mouseOffset = { x: event.clientX, y: event.clientY };
      // 모달의 현재 위치가 없을 경우 기본값을 0으로 설정
      dialogOffset = {
        left: modalDialog.style.left === '' ? 800 : Number(modalDialog.style.left.replace('px', '')),
        right: modalDialog.style.top === '' ? 400 : Number(modalDialog.style.top.replace('px', ''))
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
    $('#modalTitle').text(status == 'add'?'신규 추가':''); //모달창의 제목 변경

    // form 전체 초기화
    //if(status =='add'){
        $("form[name='form"+id+"']").each(function() {
            this.reset();
            $("input[type=hidden]").val(''); //reset만으로 hidden type은 리셋이 안되기 때문에 써줌

            // 파일 입력 초기화
            $(this).find('input[type="file"]').each(function() {
                $(this).val('');  // 파일 입력 초기화
            });

            $('#modalFileName span').text('');

            // readOnly 속성 초기화
            $(this).find('input, textarea, select').each(function() {
                // readOnly 속성 제거 (읽기 전용 상태를 해제)
                $(this).removeAttr('readonly');
            });
            $(this).find('[style*="display: none"]').each(function() {
                $(this).css('display', 'block');  // 보이게 하기
            });
        });
    //}
}

//모달 열기
function modal_open(id, status, sUrl, param){
    overlay.style.display = "block"; // 오버레이 배경 표시
    $("#modalState").val(status); // status 저장
    if(status !='add'){
        var sUrl = sUrl;
        var data = {};  // 전송할 데이터 객체 생성

        // param1, param2, ...로 data 구성
        if(param != null && typeof param === 'string' && param.includes("|")){
            var params = param.split("|");
            //console.log("param:"+param + "//////"+params.length);
            for (var i = 0; i < params.length; i++) {
                data["param" + (i + 1)] = params[i];  // param1, param2, ... 형식으로 추가
            }
        }else{
            data = {param1 : param};
        }
        $.ajax({
            type: "get",
            url:   sUrl,
            data: data,
            success: function(res) {
                console.log("요청성공", res);

                if (res != null) {
                    $("#" + id).fadeIn();
                    modal_init(id, status);

                    // res 데이터를 다른 함수로 전달하여 데이터 바인딩 처리
                    if(status === 'second'){ //두번째 모달창을 여는 경우
                        bindModalData2(status, res);
                    }else{
                        bindModalData(status, res);
                    }
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
    overlay.style.display = "none"; // 오버레이 숨기기
    $("#" + id).fadeOut();
    //초기화
    modal_init(id, 'add');
}


//2024.11.08 sylee 이사왔음.
// updatePagination(response.pageMaker);
// 페이지네이션 처리 함수 (updatePagination)
function updatePagination(pageMaker) {
    var paginationHtml = '';
    var currentPage = pageMaker.criteria.pageNum;
    var startPage = pageMaker.startPage;
    var endPage = pageMaker.endPage;
    var totalPages = pageMaker.totalPages;

    // 이전 페이지 버튼
    if (currentPage > 1) {
        paginationHtml += `<a href="#" class="prev" data-page="${currentPage - 1}">&lt;&lt;</a>`;
    }

    // 페이지 번호
    for (var i = startPage; i <= endPage; i++) {
        if (i === currentPage) {
            paginationHtml += `<a href="#" class="page active" data-page="${i}">${i}</a>`;
        } else {
            paginationHtml += `<a href="#" class="page" data-page="${i}">${i}</a>`;
        }
    }

    // 다음 페이지 버튼
    if (currentPage < totalPages) {
        paginationHtml += `<a href="#" class="next" data-page="${currentPage + 1}">&gt;&gt;</a>`;
    }

    // 페이지네이션 HTML 업데이트
    $('#pagination').html(paginationHtml);

    // 페이지 번호 클릭 시, 해당 페이지로 데이터 로드
    $('.page, .prev, .next').on('click', function(event) {
        event.preventDefault();
        var pageNum = $(this).data('page');
        searchCompanies(pageNum);
    });
}


function modalConfirmClose(id){
    $("#" + id).fadeOut();
}

function confirmAction(isConfirmed) {
    if (window.confirmCallback) {
        window.confirmCallback(isConfirmed);
    }
    modalConfirmClose('dialogConfirm');
}

function showConfirm(type, message ,callback ) {
   var alertDiv = document.querySelector('#alertTypeimg');
   alertDiv.setAttribute('data-type', type);  // 'success' 또는 'warning' 설정
   document.getElementById('confirmMessageContent').innerText = message;  // 메시지 설정
   modal('dialogConfirm');
   window.confirmCallback = callback;
}

function showAlert(type, message ) {
   var alertDiv = document.querySelector('#alertTypeimg');
   alertDiv.setAttribute('data-type', type);  // 'success' 또는 'warning' 설정
   document.getElementById('messageContent').innerText = message;  // 메시지 설정
   modal('dialog');
}

     //모달 열기
  function modal(id){
    overlay.style.display = "block"; // 오버레이 배경 표시
    $("#" + id).fadeIn();
  }

/* 첨부파일 검증 */
function validation(obj){
    const fileTypes = ['application/pdf', 'image/gif', 'image/jpeg', 'image/png', 'image/bmp', 'image/tif', 'application/haansofthwp', 'application/x-hwp'];
    if (obj.name.length > 100) {
        alert("파일명이 100자 이상인 파일은 제외되었습니다.");
        return false;
    } else if (obj.size > (100 * 1024 * 1024)) {
        alert("최대 파일 용량인 100MB를 초과한 파일은 제외되었습니다.");
        return false;
    } else if (obj.name.lastIndexOf('.') == -1) {
        alert("확장자가 없는 파일은 제외되었습니다.");
        return false;
    } else if (!fileTypes.includes(obj.type)) {
        alert("첨부가 불가능한 파일은 제외되었습니다.");
        return false;
    } else {
        return true;
    }


    //


}

