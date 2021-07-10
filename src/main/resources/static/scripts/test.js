function loadVideo() {
    let videourl = document.getElementById("input_field").innerHTML;
    videourl = linkRestyle(videourl);
    document.getElementById("getvideo").src=videourl;
};

function linkRestyle(videourl){
    let processedURL="";
    let strpreced="https://www.youtube.com/embed/"
    
        videourl=videourl.substr(videourl.search("watch")+8,);
        videourl=videourl.slice( 0, 11);
        var url=processedURL.concat(strpreced,videourl); 
        return url;
   
    
}
// function linkRestyle(videourl){
// 
//     if(!videourl.includes("https://")){
//         videourl = "https://" + videourl;
//     }
//     if(videourl.includes("youtu.be")){
//         videourl = videourl.substring(0, 8) + "www.youtube.com/embed" + videourl.substring(16);
//     }
//     else{
//         if(videourl.includes("watch?")){
//             var index = videourl.indexOf("watch?");
//             videourl = videourl.substring(0, index) + "embed/" + videourl.substring(index + 6);
//         }
//         if(videourl.includes("v=")){
//             index = videourl.indexOf("v=");
//             videourl = videourl.substring(0, index) + videourl.substring(index + 2);
//         }
//     }
//     return videourl;
// };
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


$(document).ready(function() {
    $('#summernote').summernote();
});