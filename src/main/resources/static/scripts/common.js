

function loadVideo2() {
  var videourl = document.getElementById("input_field2").value;
  if(videourl === ""){
      alert("There is no URL provided, please enter your link and try again");
      return false;
  }
  else{
  
      var url = srcAPI(videourl);
      
      document.getElementById("frameAPI").src=url;
      return true;
  }
  }
  function srcAPI(videourl){
  
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
      ['view', [ 'codeview','help']],
    ],

      width: 725,
      height: 544, disableResizeEditor:true, placeholder:'Enter notes here'});
    });


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
    var videourl = document.getElementById("input_field2").value;
    var url = srcAPI(videourl);
    var play = "&autoplay=1"
    var strsucced="&start=".concat(t,play);
    url = url.concat(strsucced);
    document.getElementById("frameAPI").src=url;
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



//test commands 
// var s = $("#summernote").summernote("code")
// $('#summernote').summernote('pasteHTML',s)


// alternative implementation for future reference 
  //   document.body.onkeyup = function(embed_ts_summernotes){
  //     if(embed_ts_summernotes.keyCode == 32){
  //       var HTMLstring = '<button id="embed_ts" onclick="Timestamp_embed(500)"></button>';
  //       $('#summernote').summernote('pasteHTML', HTMLstring);
  //     }
  // }

