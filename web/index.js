$( document ).ready(function() {
function load(){

    $("div").removeData("data");

    $.getJSON( "message.json", function( data ) {
        var items = [];
        $.each( data, function( key, val ) {
            items.push( val);
        });

        $( "<ul/>", {
            "class": "data",
            html: items.join( "" )
        }).appendTo( "body" );
    });
}

    // setInterval(load,1000);
    load();
});

