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
        ['style', ['style']],
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
//tester function to be replaced with get_time function
//that uses iframes api to return exact video playtime 
// for now evry word is 10 seconds apart
var time_in_seconds=0;
function get_timestamp(){
  time_in_seconds=time_in_seconds+10;
  return time_in_seconds;
}//returns time in t 

  
//function to load video at time t, after clicking on a timestamp . (time t should be in seconds )
  function Timestamp_embed(t){
    var videourl = document.getElementById("input_field").value;
    var url = linkRestyle(videourl);
    var play = "&autoplay=1"
    var strsucced="&start=".concat(t,play);
    url = url.concat(strsucced);
    document.getElementById("getvideo").src=url;
    }
    
    //var links= linkToTimedlink("https://www.youtube.com/watch?v=8EHEvx4eGlQ","120s")
    //returns links="https://www.youtube.com/watch?v=8EHEvx4eGlQ&start=120s"

// converts html to add timestamps into a string, to be injected into summernotes text editor
function inject_ts_html(){
  var t = get_timestamp();
  var strpreced ='<button id="embed_ts" class="btn btn-light" onclick="Timestamp_embed(';
  var strsucced =')"></button>';
  var html_ts = strpreced.concat(t,strsucced);
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
  })

