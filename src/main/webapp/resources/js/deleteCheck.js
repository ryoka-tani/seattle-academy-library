/**
 * 
 */
$(function() {
	$(".btn_deleteBook").on('click', function() {
		if(!confirm("本当に削除しますか？")){
			return false;
		}
	});
});  