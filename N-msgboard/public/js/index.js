$(function(){
    function maxLimit(){
            var num=$(this).val().substr(0,140);
            $(this).val(num);
            $(".words-count").text($(this).val().length+"/140");
    };
    $(".input-text").keyup(maxLimit);
});