function loadVideo() {
    var videourl = document.getElementById("getvideo").value;
    document.getElementById("getvideo").src=videourl;
};

$(document).ready(function() {
    $('#summernote').summernote();
});