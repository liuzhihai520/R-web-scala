/*processbar*/
function ProcessBar(obj){
	this.el=obj;
	this.current=this.el.data('value');
	this.init();
}
ProcessBar.prototype={
	init:function(){
		this.el.find('.current').css('width',this.current+'%')
	}
}
/*preview*/
function Preview(uploadBtn,previewContainer){
	this.uploadBtn=uploadBtn;
	this.previewContainer=previewContainer;
	this.allowExtention=".jpg,.bmp,.gif,.png";
	this.events();
}
Preview.prototype={
	getFileUrl:function(){
		if (navigator.userAgent.indexOf("MSIE")>=1) {
			this.uploadBtn.get(0).select();
			this.uploadBtn.get(0).blur();
			url = document.selection.createRange().text;
		}
		else if(navigator.userAgent.indexOf("Firefox")>0) {
			url = window.URL.createObjectURL(this.uploadBtn.get(0).files.item(0));
		} else if(navigator.userAgent.indexOf("Chrome")>0) {
			url = window.URL.createObjectURL(this.uploadBtn.get(0).files.item(0));
		}
		return url;
	},
	isFileAllow:function(){
		var extention=this.uploadBtn.get(0).value.substring(this.uploadBtn.get(0).value.lastIndexOf(".")+1).toLowerCase();
		if(this.allowExtention.indexOf(extention)>-1) return true;
		return false;
	},
	previewPic:function(fileUrl){
		var that=this;
		if(window.FileReader){
			var reader = new FileReader();
			reader.readAsDataURL(this.uploadBtn.get(0).files[0]);
			reader.onload = function(e){
				that.previewContainer.html($('<img src="'+ e.target.result+'">'));
			}
		}else{
			var sFilter='filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src="';
			var img=$('<img style="visibility: hidden;"/>');
			img.css({
				'filter':'progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=image,src='+fileUrl
			});
			this.previewContainer.html(img);
			var maxWidth=this.previewContainer.outerWidth();
			var maxHeight=this.previewContainer.outerHeight();
			var rect = this.clacImgZoomParam(maxWidth, maxHeight, img.width(), img.height());
			var div=$('<div>')
			div.css({
				'width':rect.width,
				'height':rect.height,
				'margin-top':rect.top,
				'margin-left':rect.left,
				'filter':'progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src='+fileUrl
			})
			this.previewContainer.html(div);
		}
	},
	clacImgZoomParam:function( maxWidth, maxHeight, width, height ){
		var param = {top:0, left:0, width:width, height:height};
		if( width>maxWidth || height>maxHeight )
		{
			rateWidth = width / maxWidth;
			rateHeight = height / maxHeight;

			if( rateWidth > rateHeight )
			{
				param.width = maxWidth;
				param.height = Math.round(height / rateWidth);
			}
			else
			{
				param.width = Math.round(width / rateHeight);
				param.height = maxHeight;
			}
		}

		param.left = Math.round((maxWidth - param.width) / 2);
		param.top = Math.round((maxHeight - param.height) / 2);
		return param;
	},
	events:function(){
		var that=this;
		this.uploadBtn.change(function(){
			var fileUrl=that.getFileUrl();
			var isFileAllow=that.isFileAllow();
			if(isFileAllow){
				that.previewPic(fileUrl);
			}
		})
	}

};