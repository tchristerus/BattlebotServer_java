// (function(){
    var socket = io.connect('http://battlebot.serverict.nl:6969');
    var latestUpdate = new Array();
    //  addOrUpdateBot(testBot);

    function timeoutTimer(){

        socket.emit("uptime", "");

        for (var i in latestUpdate){
            if (typeof latestUpdate[i] !== 'function') {
                if(Date.now() - latestUpdate[i] > 5000){
                    $('#table_bots > tbody > tr[mac="'+i+'"]').addClass('danger');
                    $('#table_bots > tbody > tr[mac="'+i+'"]').attr('disabled',true);
                    $('#table_bots > tbody > tr[mac="'+i+'"]').find("td").eq(4).find('button').fadeIn();
                }else{
                    $('#table_bots > tbody > tr[mac="'+i+'"]').removeClass('danger');
                    $('#table_bots > tbody > tr[mac="'+i+'"]').attr('disabled',false);
                    $('#table_bots > tbody > tr[mac="'+i+'"]').find("td").eq(4).find('button').fadeOut();

                }
            }
        }
    };

    setInterval(timeoutTimer, 1000);
    socket.on('connected', function (data) {
        $("h1").html('Connection established')
    });

    socket.on('update_bot', function(data){
        addOrUpdateBot(data);
    });

    socket.on('uptime', function(data){
        $("#time").html("Server uptime: " + data)
    });

    socket.on('disconnect', function(data){
        $("h1").html(data)
    });

    socket.on('console_message', function(data){
        $('#console').append("<tr><td>> " + data + "</td></tr>")
        $(".console").stop().animate({ scrollTop: $(".console")[0].scrollHeight}, 1000);
    });



    function sendCharToBot(botname, char){
        socket.emit("send", "{bot: " + botname + ", message: " + char + "}")
    }
    // als verbinden failed (server kant) dan moet er een event gestuurd worden naar de client dat het niet is gelukt en de knop weer enabled moet / text updaten van de knop

    socket.on('connection_failed', function(data){
        var jsonobj = $.parseJSON(data);
        $("button#" + jsonobj.mac).html("Opniew verbinden");
        $("button#" + jsonobj.mac).attr("disabled", false);
    });

    $(document).ready(function(){
        $("#btnConnect").click(function(){
            reconnect($("#connectBot").val(), null);
        });

        addControlerButton("up", 1, 38, true);
        addControlerButton("right", 2, 39, true);
        addControlerButton("down", 3, 40, true);
        addControlerButton("left", 4, 37, true);
        addControlerButton("stop", 5, 37, false);
        addControlerButton("square", 6, 37, false);
        addControlerButton("triangle", 7, 37, false);
        addControlerButton("circle", 8, 37, false);
        addControlerButton("start", 9, 37, false);
        addControlerButton("cross", 10, 37, false);

        function addControlerButton(id, action, keyboardKey, sendStop){
            $("#" + id).mousedown(function () {
                sendCharToBot($("#botNameField").val(),action);
            });

            $("#" + id).on('touchstart', function () {
                sendCharToBot($("#botNameField").val(),action);
            });

            if(sendStop){
                $("#" + id).mouseup(function () {
                    sendCharToBot($("#botNameField").val(),5);
                });

                $("#" + id).on('touchend', function () {
                    sendCharToBot($("#botNameField").val(),action);
                });
            }
        }
    });

    // $("body").keydown(function (data) {
    //     switch(data.which){
    //         case 38:
    //             sendCharToBot($("#botNameField").val(),action);
    //             break;
    //     }
    // });
    // $("body").keyup(function (data) {
    //     sendCharToBot($("#botNameField").val(),5);
    // });


    function close(){
        socket.emit("close", "");
        console.info("Close command send...")
    }

    function search(){
        socket.emit("search", "");
        console.info("Search command send...")
    }
    function reconnect(name, mac){
        socket.emit("reconnectEvent", name);
        if(mac){
            $("button#" +mac).html("Verbinden...");
            $("button#" +mac).attr("disabled", true);
        }
    }

    function addOrUpdateBot(jsonString){
        var jsonobj = $.parseJSON(jsonString)
        var alreadyExist = false;

        // Searching for existing row with this mac
        $('#table_bots > tbody  > tr').each(function() {
            if($(this).attr("mac") == jsonobj.mac){
                $(this).find("td").eq(1).html(jsonobj.speed);
                $(this).find("td").eq(2).html(jsonobj.distance);
                $(this).find("td").eq(3).html(jsonobj.time);
                $("button#" +jsonobj.mac).html("Opnieuw verbinden");
                $("button#" +jsonobj.mac).attr("disabled", false);
                alreadyExist = true;
                latestUpdate[jsonobj.mac] = Date.now();
            }
        });

        //console.info(alreadyExist);
        // Bot did not yet exist... creating new row
        if(!alreadyExist){
            //console.info('<tr mac="' + jsonobj.mac + '"><td>' + jsonobj.name + '</td><td>' + jsonobj.speed + '</td><td>' + jsonobj.distance + '</td><td>' + jsonobj.time + '</td></tr>');
            $('#table_bots > tbody').append('<tr mac="' + jsonobj.mac + '"><td>' + jsonobj.name + '</td><td>' + jsonobj.speed + '</td><td>' + jsonobj.distance + '</td><td>' + jsonobj.time + '</td><td> <button class="btn btn-info" id=\''+ jsonobj.mac + '\' onclick="reconnect(\''+ jsonobj.name +'\', this.id)">Opnieuw verbinden</button></td></tr>');
            latestUpdate[jsonobj.mac] = Date.now();
        }
    }
// })();
