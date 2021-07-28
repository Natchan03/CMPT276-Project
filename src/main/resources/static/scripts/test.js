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
    if((videourl.includes("youtube") || videourl.includes("youtu.be")) && videourl.includes("watch"))
    {
        videourl=videourl.substr(videourl.search("watch")+8,);
        videourl=videourl.slice( 0, 11);
        var url=processedURL.concat(strpreced,videourl);

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
    $('#summernote').summernote({width: 725,
        height: 544, disableResizeEditor:true, placeholder:'Enter notes here'});
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

//  function linkRestyle(videourl){

//      if(!videourl.includes("https://")){
//          videourl = "https://" + videourl;
//      }
//      if(videourl.includes("youtu.be")){
//          videourl = videourl.substring(0, 8) + "www.youtube.com/embed" + videourl.substring(16);
//      }
//      else{
//          if(videourl.includes("watch?")){
//              var index = videourl.indexOf("watch?");
//              videourl = videourl.substring(0, index) + "embed/" + videourl.substring(index + 6);
//          }
//          if(videourl.includes("v=")){
//              index = videourl.indexOf("v=");
//              videourl = videourl.substring(0, index) + videourl.substring(index + 2);
//          }
//      }
//      return videourl;
//  };
//for future improvents of linkRestyle function---
//instead of using linkRestyle here's a simple solution which,
// take a url as input,conducts -> boolean search for ((youtu.be or youtube) and watch) in the input link to assess its validity
// and then converts a valid link to one usable by iFrame api
// by extracting video ID from a valid link, which could be appended to https://www.youtube.com/embed/ to get a iFrame compatible url.
//heres a console example implemented to search only for watch in the link and convert it to iFram compatible form.
//let str = "https://www.youtube.com/watch?v=cCBSsh3whvU";
//let strpreced= "https://www.youtube.com/embed/"
//let processedURL=""
//str=str.substr(str.search("watch")+8, );
//var url = processedURL.concat(strpreced,str);

/* WORK IN PROGRESS
function timeStamp(){
    ytplayer = document.getElementsByTagName("#getvideo");
    var HTMLString = "<p>"+ ytplayer.getCurrentTime + "</p>";
    $('#summernote').summernote('pasteHTML', HTMLString);
};
*/

// function below takes a videourl, and a timestamp T from timestamp function above in format(XmYs where X and y are numbers) to convert it into a timed youtube link
function linkToTimedlink(videourl,t){

    var processedURL="";
    var strpreced="https://www.youtube.com/watch?v="  // reprocessing of the link
    var strsucced="&t=".concat(t)
    if((videourl.includes("youtube") || videourl.includes("youtu.be")) && videourl.includes("watch"))
    {
        videourl=videourl.substr(videourl.search("watch")+8,);
        videourl=videourl.slice( 0, 11);
        var url=processedURL.concat(strpreced,videourl);
        var url = url.concat(strsucced);
        return url;
    }
}
//var links= linkToTimedlink("https://www.youtube.com/watch?v=8EHEvx4eGlQ","2m40s")
//returns links="https://www.youtube.com/watch?v=8EHEvx4eGlQ&t=2m40s"



