//Ignore for now
function search(){
        var search_tag = document.getElementById("input_field").value;
        var regExp = /[a-zA-Z0-9]/g;
        if(search_tag === "" || !regExp.test(search_tag)){
            return false;
        } else{
            var table = document.getElementById("notes_table");
            var rowlength = table.rows.length;
            var table_array = [];
            for(var i = 0; i < rowlength; i++){
                table_array[i] = [];
                var cells = table.rows.item(i).cells;
                var cellslength = cells.length;
                for(var j = 0; j < cellslength; j++){
                    table_array[i][j] = cells.item(j).innerHTML;
                }
            }
            for(var i = 1; i < rowlength; i++){
                if(!table_array[i][1].includes(search_tag)){
                    table_array.splice(i, 1);
                    i--;
                    rowlength--;
                }
            }
            console.log(document.getElementById("notes_table"));
            $("#notes_table tbody").remove();
            console.log(document.getElementById("notes_table"));
            return true;
        }
}