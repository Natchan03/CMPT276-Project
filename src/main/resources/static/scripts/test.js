function loadVideo() {
    var videourl = document.getElementById("input_field").value;
    videourl = linkRestyle(videourl);
    document.getElementById("getvideo").src=videourl;
};

function linkRestyle(videourl){
    if(!videourl.includes("https://")){
        videourl = "https://" + videourl;
    }
    if(videourl.includes("youtu.be")){
        videourl = videourl.substring(0, 8) + "youtube.com/embed/" + videourl.substring(16);
    }
    else{
        if(videourl.includes("watch?")){
            var index = videourl.indexOf("watch?");
            videourl = videourl.substring(0, index) + "embed/" + videourl.substring(index + 6);
        }
        if(videourl.includes("v=")){
            index = videourl.indexOf("v=");
            videourl = videourl.substring(0, index) + videourl.substring(index + 2);
        }
    }
    return videourl;
};

$(document).ready(function() {
    $('#summernote').summernote();
});