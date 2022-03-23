var className = "repository-person-info";
var containerName = className + "-container";
var iframeName = className + "-iframe";
var iframeCloseName = className + "-close";

var InfoArr = document.getElementsByClassName(className);
var elements = 0;

var getInfo = function() {
    thisEl = this
    var attribute = thisEl.getAttribute(className);
    if(document.getElementById(containerName) && attribute != '') {
        var oXmlHttp = new XMLHttpRequest();
	oXmlHttp.onreadystatechange = function () {
            if(oXmlHttp.readyState == XMLHttpRequest.DONE) {
                if(oXmlHttp.status == 200) {
                    iframe = document.getElementById(iframeName);
                    iframe.src = "https://crai-app.urosario.edu.co/repository-person-info/repository-person-info.php?cedula=" + attribute;

                    container = document.getElementById(containerName);
                    container.style.display = "block";
                    container.style.zIndex = "10000";
                    container.style.height = "400px";
        
                    const rect = thisEl.getBoundingClientRect();

                    container.style.top = parseInt(rect.top + window.scrollY + 10) + "px";
                    container.style.left = parseInt(rect.left + window.scrollX + thisEl.offsetWidth) + "px";
                } else {
                    closeIframe();
	        }
            }
        }
        oXmlHttp.open('get', 'https://crai-app.urosario.edu.co/repository-person-info/repository-person-info.php?cedula='+attribute);
        oXmlHttp.send();

    } else {
        closeIframe();
    }
};

var closeIframe = function() {
    if(document.getElementById(containerName)) {
        iframe = document.getElementById(iframeName);
        iframe.src = "";

        container = document.getElementById(containerName);
        container.style.display = "none";
        container.style.zIndex = "-10000";
    }
}

for (var i = 0; i < InfoArr.length; i++) {
    InfoArr[i].addEventListener("mouseover", getInfo, false);
    elements++;
}

if(elements > 0) {
    var iframeContainter = document.createElement("div");
    iframeContainter.id = containerName;
    iframeContainter.style.border = "4px solid rgb(59, 59, 59)";
    iframeContainter.style.display = "none";
    iframeContainter.style.position = "absolute";
    iframeContainter.style.zIndex = "-1000";
    iframeContainter.style.width = "450px";
    iframeContainter.style.boxShadow = "#000 0px 0px 20px";
    iframeContainter.style.borderRadius = "10px";
    
    var iframeInfo = document.createElement("iframe");
    iframeInfo.id = iframeName;
    iframeInfo.style.height = "100%";
    iframeInfo.style.border = "none";
    iframeInfo.style.width = "100%";
    iframeInfo.style.borderRadius = "10px";

    var iframeClose = document.createElement("div");
    iframeClose.id = iframeCloseName;
    iframeClose.style.color = "#F00";
    iframeClose.style.height = "32px";
    iframeClose.style.position = "absolute";
    iframeClose.style.top = "5px";
    iframeClose.style.right = "10px";
    iframeClose.style.cursor = "pointer"; 
    iframeClose.addEventListener("click", closeIframe, false);
    iframeClose.innerHTML = "<strong><em>Cerrar</em></strong>";

    document.getElementsByTagName("body")[0].appendChild(iframeContainter);
    document.getElementById(containerName).appendChild(iframeInfo);
    document.getElementById(containerName).appendChild(iframeClose);
}
