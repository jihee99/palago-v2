// function checkForm(){
//
//     let mbrId = document.getElementById('mbrId').value;
//     let mbrPwd = document.getElementById('mbrPwd').value;
//
//     if(mbrId === '' || mbrPwd === ''){
//         alert("아이디와 비밀번호를 모두 입력해주세요.");
//         return false;
//     }
//
//     return true;
// }
"use strict";
(function ($) {

    $(document).on('keypress', function (e) {
        if (e.which === 13) {
            // Enter 키를 눌렀을 때
            $('#loginBtn').click();
        }
    });

    $('#loginBtn').on('click',function (e){
        let mbrId = $('#mbrId').value;
        let mbrPwd = $('#mbrPwd').value;

        if(mbrId === '' || mbrPwd === ''){
            alert("아이디와 비밀번호를 모두 입력해주세요.");
        }else{
            let formData = $('#loginForm').serializeObject();
            $.ajax({
                type: 'POST',
                url: '/login',
                contentType: 'application/json',
                data: JSON.stringify(formData),
                success: function (response){
                    console.log(response)
                    window.location.href = '/';
                },
                error: function (error){
                    alert("실패")
                }
            });

        }

    });
})(jQuery);
