
/*********************样式模板选择*****************************/
var input = document.getElementById("select");
function selectTheme() {
	var theme = input.options[input.selectedIndex].innerHTML;
	editor.setOption("theme", theme);
}
var choice = document.location.search
		&& decodeURIComponent(document.location.search.slice(1));
if (choice) {
	input.value = choice;
	editor.setOption("theme", choice);
}
/*********************样式自动提示插件*****************************/
var dummy = {
	attrs : {
		color : [ "red", "green", "blue", "purple", "white", "black", "yellow" ],
		size : [ "large", "medium", "small" ],
		description : null
	},
	children : []
};

var tags = {
	"!top" : [ "top" ],
	"!attrs" : {
		id : null,
		class : [ "A", "B", "C" ]
	},
	top : {
		attrs : {
			lang : [ "en", "de", "fr", "nl" ],
			freeform : null
		},
		children : [ "animal", "plant" ]
	},
	animal : {
		attrs : {
			name : null,
			isduck : [ "yes", "no" ]
		},
		children : [ "wings", "feet", "body", "head", "tail" ]
	},
	plant : {
		attrs : {
			name : null
		},
		children : [ "leaves", "stem", "flowers" ]
	},
	wings : dummy,
	feet : dummy,
	body : dummy,
	head : dummy,
	tail : dummy,
	leaves : dummy,
	stem : dummy,
	flowers : dummy
};

function completeAfter(cm, pred) {
	var cur = cm.getCursor();
	if (!pred || pred())
		setTimeout(function() {
			if (!cm.state.completionActive)
				cm.showHint({
					completeSingle : false
				});
		}, 100);
	return CodeMirror.Pass;
}

function completeIfAfterLt(cm) {
	return completeAfter(cm, function() {
		var cur = cm.getCursor();
		return cm.getRange(CodeMirror.Pos(cur.line, cur.ch - 1), cur) == "<";
	});
}

function completeIfInTag(cm) {
	return completeAfter(
			cm,
			function() {
				var tok = cm.getTokenAt(cm.getCursor());
				if (tok.type == "string"
						&& (!/['"]/.test(tok.string
								.charAt(tok.string.length - 1)) || tok.string.length == 1))
					return false;
				var inner = CodeMirror.innerMode(cm.getMode(), tok.state).state;
				return inner.tagName;
			});
}
/*********************启用开启编辑器*****************************/
var editor = CodeMirror.fromTextArea(document.getElementById("content"), {
	model:"text/html/xml",       //样式自动提示插件
	lineNumbers : true,
	viewportMargin : Infinity,
	styleActiveLine : true,
	matchBrackets : true ,
    theme: "eclipse",
	autoCloseTags: true,		 //自动补齐</..>		
	foldGutter: true,			 // fold折叠 
	lineWrapping: true,			 // fold折叠 
	extraKeys: {     			
		      //样式自动提示插件
	          "'<'": completeAfter,
	          "'/'": completeIfAfterLt,
	          "' '": completeIfInTag,
	          "'='": completeIfInTag,
	          "Ctrl-Space": "autocomplete",
	          //F11全屏模式
	          "F11": function(cm) {
	              cm.setOption("fullScreen", !cm.getOption("fullScreen"));
	            },
	          "Esc": function(cm) {
	             if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
	          },
	          // fold折叠 
	          "Ctrl-Q": function(cm){ cm.foldCode(cm.getCursor()); }
	 },
	 // fold折叠 
	gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
	//样式自动提示插件
	hintOptions: {schemaInfo: tags} 
});
editor.foldCode(CodeMirror.Pos(21, 0));
/********************* 显示高亮预览 *****************************/
function doHighlight(){
		var content   = editor.getValue();
		if (!content) {
			alert("模板内容为空");
			return false;
		}
	  CodeMirror.runMode(content, "application/xml",
	                     document.getElementById("outputcontext"));
	  $(".closeHighlight").show();
	  $(".doHighlight").hide();
	  $("#outputcontext").show();
}
 function closeHighlight(){
	 $(".closeHighlight").hide();
	 $(".doHighlight").show();
	 $("#outputcontext").hide();
 }
 /*********************   *****************************/
 
/********************* *****************************/
 $(function(){ 
	 //保存提交 取值注入TextArea域中
	 $(".add_seller_btn").click(function(){
		 $("#content").text(editor.getValue()); 
		 return true;
	 });
 })
 
 /********************* *****************************/
