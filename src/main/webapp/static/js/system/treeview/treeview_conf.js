var nodeCheckedSilent = false;
function nodeChecked (event, node){
    if(nodeCheckedSilent){
        return;
    }
    nodeCheckedSilent = true;
    checkAllParent(node);
    checkAllSon(node);
    nodeCheckedSilent = false;
}

var nodeUncheckedSilent = false;
function nodeUnchecked  (event, node){
    if(nodeUncheckedSilent)
        return;
    nodeUncheckedSilent = true;
    uncheckAllParent(node);
    uncheckAllSon(node);
    nodeUncheckedSilent = false;
}

function checkAllParent(node){
    $('#resourceList').treeview('checkNode',node.nodeId,{silent:true});
    var parentNode = $('#resourceList').treeview('getParent',node.nodeId);
    if(!("nodeId" in parentNode)){
        return;
    }else{
        checkAllParent(parentNode);
    }
}
function uncheckAllParent(node){

}

function checkAllSon(node){
    $('#resourceList').treeview('checkNode',node.nodeId,{silent:true});
    if(node.nodes!=null&&node.nodes.length>0){
        for(var i in node.nodes){
            checkAllSon(node.nodes[i]);
        }
    }
}
function uncheckAllSon(node){
    $('#resourceList').treeview('uncheckNode',node.nodeId,{silent:true});
    if(node.nodes!=null&&node.nodes.length>0){
        for(var i in node.nodes){
            uncheckAllSon(node.nodes[i]);
        }
    }
}