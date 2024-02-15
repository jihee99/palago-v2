"use strict";
(function ($) {
    $('#join-btn').on('click', function(e){
        let mbrId = document.getElementById('mbrId').value;
        let mbrPwd = document.getElementById('mbrPwd').value;
        let mbrName = document.getElementById('mbrName').value;
        let mbrPno = document.getElementById('mbrPno').value;

        if(mbrId === '' || mbrPwd === '' || mbrName === '' || mbrPno === ''){
            alert("미입력된 항목을 확인해주세요.");
            return false;
        }

        let formData = $('#join-form').serializeObject();

        console.log(formData)
        $.ajax({
            type: 'POST',
            url: '/sign/new',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function (response){
                alert("success");
                window.location.href = '/sign/in';
            },
            error: function (error){
                alert("실패")
            }
        });
    })
})(jQuery);
