function check(type){
    var text = document.getElementById("deleteBox").value;
    if(text !== "DELETE"){
        alert("DELETE is not typed properly");
        return false;
    }
    if(type === "admin"){
        alert("You can not delete admin account");
        return false;
    }
    return true;
}

function deleteUserAccount(){
    var text = document.getElementById("deleteBox").value;
    if(text !== "DELETE"){
        alert("DELETE is not typed properly");
        return false;
    } 
    return true
}