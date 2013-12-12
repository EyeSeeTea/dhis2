$(function () {

    var $contextMenu = $("#contextMenu");
    var $rowClicked;

    $("body").on("contextmenu", "table tr", function (e) {
        $rowClicked = $(this);
        $contextMenu.css({
            display: "block",
            left: e.pageX,
            top: e.pageY
        });
        return false;
    });

    $contextMenu.on("click", "a", function () {
        var message = "You clicked on the row '" + 
            $rowClicked.children("*")[1].innerHTML + "'\n";
        message += "And selected the menu item '" + $(this).text() + "'";
        alert(message);
        $contextMenu.hide();
    });

    $(document).click(function () {
        $contextMenu.hide();
    });

});