function loadVideo() {
    var videourl = document.getElementById("input_field").value;
    if(videourl === ""){
        alert("There is no URL provided, please enter your link and try again");
        return false;
    }
    else{
        videourl = linkRestyle(videourl);
        document.getElementById("getvideo").src=videourl;
        return true;
    }
};
 // 2. This code loads the IFrame Player API code asynchronously.
 var tag = document.createElement('script');

 tag.src = "https://www.youtube.com/iframe_api";
 var firstScriptTag = document.getElementsByTagName('script')[0];
 firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
 
 // 3. This function creates an <iframe> (and YouTube player)
 //    after the API code downloads.
 var player;
 function onYouTubeIframeAPIReady() {
 
 }
       function loadVideo2() {
           var videourl = document.getElementById("input_field").value;
           if(videourl === ""){
            alert("There is no URL provided, please enter your link and try again");
            return false;
            }
            else{
           var videoid = getvideoID(videourl);
           if (player == undefined) {
               player = new YT.Player('player', {
                   height: '595',
                   width: '725',
                   videoId: videoid,
                   playerVars: {
                   'playsinline': 1
                   },
                   events: {
                   'onReady': onPlayerReady,
                   'onStateChange': onPlayerStateChange
                   }
               });
           } else {
               player.loadVideoById(videoid, 0, "large");
           }}
     };
 
 // 4. The API will call this function when the video player is ready.
 function onPlayerReady(event) {
 event.target.playVideo();
 }
 
 // 5. The API calls this function when the player's state changes.
 //    The function indicates that when playing a video (state=1),
 //    the player should play for six seconds and then stop.
 var done = false;
 function onPlayerStateChange(event) {
 if (event.data == YT.PlayerState.PLAYING && !done) {
  
   done = true;
 }
 }
 function stopVideo() {
 player.stopVideo();
 }


function linkRestyle(videourl){

    var processedURL="";
    var strpreced="https://www.youtube.com/embed/"
    var strsucced="?enablejsapi=1"
    if((videourl.includes("youtube") || videourl.includes("youtu.be")) && videourl.includes("watch"))
    {
        videourl=videourl.substr(videourl.search("watch")+8,);
        videourl=videourl.slice( 0, 11);
        var url=processedURL.concat(strpreced,videourl);
        var url = url.concat(strsucced);
  

        return url;
    } else if(videourl.includes("youtu.be") && !videourl.includes("watch")){
        videourl=videourl.substr(videourl.search("youtu.be/")+9,);
        videourl=videourl.slice( 0, 11);
        var url=processedURL.concat(strpreced,videourl);
        var url = url.concat(strsucced);
        return url;
    }
    else{alert("The URL provided is invalid, please try again with a valid Youtube video URL")}

}


//extracts videoid from a youtube url
function getvideoID() {

    var videourl = document.getElementById("input_field").value;
    videourl=videourl.substr(videourl.search("watch")+8,);
    var videoid=videourl.slice( 0, 11);
    return videoid;
}

//load summernote api


$(document).ready(function() {
    $('#summernote').summernote({
  
      toolbar: [
        ['font', ['bold', 'underline', 'clear']],
        ['fontname', ['fontname']],
        ['color', ['color']],
        ['para', ['ul', 'ol', 'paragraph']],
        ['table', ['table']],
        ['insert', ['link', 'picture']],
        ['view', [ 'help']],
      ],
  
        width: 725,
        height: 543, disableResizeEditor:true, placeholder:'Enter notes here'});
      });
  

// returns data in summernotes text editor as html in as a string
// tested for links formatted with a sperate show as for eg https://www.youtube.com/watch?v=ARbZKCI6EFY&t=90s link, appears in summernotes as 1m:30s
//get notes will return below,
//"<p><a href=\"https://www.youtube.com/watch?v=ARbZKCI6EFY&amp;t=90s\" target=\"_blank\" style=\"background-color: rgb(255, 255, 255); font-size: 1rem;\">1m:30s</a></p><p><br></p><p><br></p>"

function getNotes() {
    if(document.getElementById("note_title").value === ""){
        alert("Can't save notes without title, please add title and try again");
        return false;
    }
    else if($($("#summernote").summernote("code")).text() === ""){
        alert("Can't save empty notes, please add text and try again");
        return false;
    }
    else{
        $('#setNoteId').val($("#noteId").val());
        $('#setVideoId').val($("#input_field").val());
        $('#setNoteTitle').val($("#note_title").val());
        $('#noteText').val($("#summernote").summernote("code"));
        return true;
    }

}

// load notes will take output saved by getNotes into the data base
// inject the data back into summernotes as formatted by user before executing getNotes()

function loadNotes(txt) {

    $('#summernote').summernote('editor.pasteHTML',txt);

}


/* WORK IN PROGRESS
function timeStamp(){
    ytplayer = document.getElementsByTagName("#getvideo");
    var HTMLString = "<p>"+ ytplayer.getCurrentTime + "</p>";
    $('#summernote').summernote('pasteHTML', HTMLString);
};
*/
  
//function to load video at time t, after clicking on a timestamp . (time t should be in seconds )
  function Timestamp_embed(time_in_seconds){
    var videourl = document.getElementById("input_field").value;
    var videoid = getvideoID(videourl);
    player.loadVideoById(videoid, time_in_seconds,"large");
    }
    
    //var links= linkToTimedlink("https://www.youtube.com/watch?v=8EHEvx4eGlQ","120s")
    //returns links="https://www.youtube.com/watch?v=8EHEvx4eGlQ&start=120s"

// converts html to add timestamps into a string, to be injected into summernotes text editor
function inject_ts_html(){
  var time_in_seconds=player.getCurrentTime();
  var strpreced ='<button id="embed_ts" type="button" class="btn btn-light" onclick="Timestamp_embed(';
  var strsucced =')">.</button>';
  var html_ts = strpreced.concat(time_in_seconds,strsucced);
  return html_ts;
}


// on press Enter, start a section as timestamp and end to start a new one when Enter is pressed again
var total_func_calls=0
    document.addEventListener('keydown', (e) => {
      if (e.code === "Enter" ||total_func_calls === 0) {
        var HTMLstring = inject_ts_html();
        $('#summernote').summernote('pasteHTML', HTMLstring);
        total_func_calls++;
      }
  });


