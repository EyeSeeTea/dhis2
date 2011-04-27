﻿/*
Copyright (c) 2003-2011, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.themes.add('default',(function(){function a(b,c){var d,e;e=b.config.sharedSpaces;e=e&&e[c];e=e&&CKEDITOR.document.getById(e);if(e){var f='<span class="cke_shared " dir="'+b.lang.dir+'"'+'>'+'<span class="'+b.skinClass+' '+b.id+' cke_editor_'+b.name+'">'+'<span class="'+CKEDITOR.env.cssClass+'">'+'<span class="cke_wrapper cke_'+b.lang.dir+'">'+'<span class="cke_editor">'+'<div class="cke_'+c+'">'+'</div></span></span></span></span></span>',g=e.append(CKEDITOR.dom.element.createFromHtml(f,e.getDocument()));if(e.getCustomData('cke_hasshared'))g.hide();else e.setCustomData('cke_hasshared',1);d=g.getChild([0,0,0,0]);!b.sharedSpaces&&(b.sharedSpaces={});b.sharedSpaces[c]=d;b.on('focus',function(){for(var h=0,i,j=e.getChildren();i=j.getItem(h);h++){if(i.type==CKEDITOR.NODE_ELEMENT&&!i.equals(g)&&i.hasClass('cke_shared'))i.hide();}g.show();});b.on('destroy',function(){g.remove();});}return d;};return{build:function(b,c){var d=b.name,e=b.element,f=b.elementMode;if(!e||f==CKEDITOR.ELEMENT_MODE_NONE)return;if(f==CKEDITOR.ELEMENT_MODE_REPLACE)e.hide();var g=b.fire('themeSpace',{space:'top',html:''}).html,h=b.fire('themeSpace',{space:'contents',html:''}).html,i=b.fireOnce('themeSpace',{space:'bottom',html:''}).html,j=h&&b.config.height,k=b.config.tabIndex||b.element.getAttribute('tabindex')||0;if(!h)j='auto';else if(!isNaN(j))j+='px';var l='',m=b.config.width;if(m){if(!isNaN(m))m+='px';l+='width: '+m+';';}var n=g&&a(b,'top'),o=a(b,'bottom');n&&(n.setHtml(g),g='');o&&(o.setHtml(i),i='');var p=CKEDITOR.dom.element.createFromHtml(['<span id="cke_',d,'" class="',b.skinClass,' ',b.id,' cke_editor_',d,'" dir="',b.lang.dir,'" title="',CKEDITOR.env.gecko?' ':'','" lang="',b.langCode,'"'+(CKEDITOR.env.webkit?' tabindex="'+k+'"':'')+' role="application"'+' aria-labelledby="cke_',d,'_arialbl"'+(l?' style="'+l+'"':'')+'>'+'<span id="cke_',d,'_arialbl" class="cke_voice_label">'+b.lang.editor+'</span>'+'<span class="',CKEDITOR.env.cssClass,'" role="presentation"><span class="cke_wrapper cke_',b.lang.dir,'" role="presentation"><table class="cke_editor" border="0" cellspacing="0" cellpadding="0" role="presentation"><tbody><tr',g?'':' style="display:none"',' role="presentation"><td id="cke_top_',d,'" class="cke_top" role="presentation">',g,'</td></tr><tr',h?'':' style="display:none"',' role="presentation"><td id="cke_contents_',d,'" class="cke_contents" style="height:',j,'" role="presentation">',h,'</td></tr><tr',i?'':' style="display:none"',' role="presentation"><td id="cke_bottom_',d,'" class="cke_bottom" role="presentation">',i,'</td></tr></tbody></table><style>.',b.skinClass,'{visibility:hidden;}</style></span></span></span>'].join(''));
p.getChild([1,0,0,0,0]).unselectable();p.getChild([1,0,0,0,2]).unselectable();if(f==CKEDITOR.ELEMENT_MODE_REPLACE)p.insertAfter(e);else e.append(p);b.container=p;p.disableContextMenu();b.on('contentDirChanged',function(q){var r=(b.lang.dir!=q.data?'add':'remove')+'Class';p.getChild(1)[r]('cke_mixed_dir_content');var s=this.sharedSpaces&&this.sharedSpaces[this.config.toolbarLocation];s&&s.getParent().getParent()[r]('cke_mixed_dir_content');});b.fireOnce('themeLoaded');b.fireOnce('uiReady');},buildDialog:function(b){var c=CKEDITOR.tools.getNextNumber(),d=CKEDITOR.dom.element.createFromHtml(['<div class="',b.id,'_dialog cke_editor_',b.name.replace('.','\\.'),'_dialog cke_skin_',b.skinName,'" dir="',b.lang.dir,'" lang="',b.langCode,'" role="dialog" aria-labelledby="%title#"><table class="cke_dialog',' '+CKEDITOR.env.cssClass,' cke_',b.lang.dir,'" style="position:absolute" role="presentation"><tr><td role="presentation"><div class="%body" role="presentation"><div id="%title#" class="%title" role="presentation"></div><a id="%close_button#" class="%close_button" href="javascript:void(0)" title="'+b.lang.common.close+'" role="button"><span class="cke_label">X</span></a>'+'<div id="%tabs#" class="%tabs" role="tablist"></div>'+'<table class="%contents" role="presentation">'+'<tr>'+'<td id="%contents#" class="%contents" role="presentation"></td>'+'</tr>'+'<tr>'+'<td id="%footer#" class="%footer" role="presentation"></td>'+'</tr>'+'</table>'+'</div>'+'<div id="%tl#" class="%tl"></div>'+'<div id="%tc#" class="%tc"></div>'+'<div id="%tr#" class="%tr"></div>'+'<div id="%ml#" class="%ml"></div>'+'<div id="%mr#" class="%mr"></div>'+'<div id="%bl#" class="%bl"></div>'+'<div id="%bc#" class="%bc"></div>'+'<div id="%br#" class="%br"></div>'+'</td></tr>'+'</table>',CKEDITOR.env.ie?'':'<style>.cke_dialog{visibility:hidden;}</style>','</div>'].join('').replace(/#/g,'_'+c).replace(/%/g,'cke_dialog_')),e=d.getChild([0,0,0,0,0]),f=e.getChild(0),g=e.getChild(1);f.unselectable();g.unselectable();return{element:d,parts:{dialog:d.getChild(0),title:f,close:g,tabs:e.getChild(2),contents:e.getChild([3,0,0,0]),footer:e.getChild([3,0,1,0])}};},destroy:function(b){var c=b.container,d=b.element;if(c){c.clearCustomData();c.remove();}if(d){d.clearCustomData();b.elementMode==CKEDITOR.ELEMENT_MODE_REPLACE&&d.show();delete b.element;}}};})());CKEDITOR.editor.prototype.getThemeSpace=function(a){var b='cke_'+a,c=this._[b]||(this._[b]=CKEDITOR.document.getById(b+'_'+this.name));return c;};CKEDITOR.editor.prototype.resize=function(a,b,c,d){var e=this.container,f=CKEDITOR.document.getById('cke_contents_'+this.name),g=d?e.getChild(1):e;
CKEDITOR.env.webkit&&g.setStyle('display','none');g.setSize('width',a,true);if(CKEDITOR.env.webkit){g.$.offsetWidth;g.setStyle('display','');}var h=c?0:(g.$.offsetHeight||0)-(f.$.clientHeight||0);f.setStyle('height',Math.max(b-h,0)+'px');this.fire('resize');};CKEDITOR.editor.prototype.getResizable=function(){return this.container;};
