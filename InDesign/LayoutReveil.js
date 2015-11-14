// Read the layout from a file and places all articles acordingly
//
// (c) 2015 Benjamin Schmid
// All rights reserved

var VALUESEPARATOR = ";";
var VALUES_PER_ENTRY = 7;
var MASTERSPREAD_INDEX = 0;
var MASTERSPREAD_INDEXES_LEFT = [0, 1, 2, 3];
var MASTERSPREAD_INDEXES_RIGHT = [0, 1, 2, 3];
var END_OF_SENTENCE = [".", "?", "!"];

var logfile;

// initialize the logfile
function initLog(){
    logfile = File.saveDialog("please choose log file", "All files: *.*", false);
    if(logfile){
        logfile.open("w");
    }
}

// log the given message to the logfile
// msg: the message to log
function log(msg){
    logfile && logfile.writeln(msg);
}

// reads the layoutfile
// returns an object containing information about all articles
function readLayoutfile(){
    var layout = {};
    var layoutfile = File.openDialog("please choose layout file", "All files: *.*", false);
    if(!layoutfile){
        return layout;
    }
    layout.rootpath = layoutfile.parent.selectDlg("select root folder");
    if(!layout.rootpath){
        return layout;
    }
    layoutfile.open("r");
    layout.articles = [];
    var line = "";
    while(line = layoutfile.readln()){
        var linesplit = line.split(VALUESEPARATOR);
        if(linesplit.length != VALUES_PER_ENTRY){
            alert("line: \"" + line + "\" not valid");
            continue;
        }
        var article = {
            startpage: parseInt(linesplit[0]),
            pagecount: parseInt(linesplit[1]),
            title: linesplit[2],
            author: linesplit[3],
            category: linesplit[4],
            format: linesplit[5],
            status: linesplit[6]
            };
        article.filename = (article.startpage < 10 ? "0" : "") + article.startpage
            + "_" + article.title.replace(/ /g, "_")
            + "__" + article.author.replace(/ /g, "_")
            + "." + article.format;
        article.file = new File(layout.rootpath.absoluteURI + "/" + article.filename);

        if(!article.file.exists){
            log(article.file.absoluteURI + " does not exist" + (article.status == 1 ? " (marked)" : ""));
            article.status = 1;
        }
        
        layout.articles.push(article);
    }
    layoutfile.close();
    log("read " + layout.articles.length + " articles");
    return layout;
}

// override all textFrames that will contain the article
// page: the page to process
function overridePage(page){
    var masterPage = document.masterSpreads[MASTERSPREAD_INDEX].pages[page.side == PageSideOptions.LEFT_HAND ? 0 : 1]
    var indexes = page.side == PageSideOptions.LEFT_HAND ? MASTERSPREAD_INDEXES_LEFT : MASTERSPREAD_INDEXES_RIGHT;
    for(var j = 0; j < indexes.length; j++){
        masterPage.textFrames[indexes[j]].override(page);
        if(j > 0){
            page.textFrames[j].previousTextFrame = page.textFrames[j-1];
        }
    }
}

function getParagraphStyleByName(name){
    var styles = app.activeDocument.allParagraphStyles;
    for(var i = 0; i < styles.length; i++){
        if(styles[i].name == name){
            return styles[i];
        }
    }
}

function getCharacterStyleByName(name){
    var styles = app.activeDocument.allCharacterStyles;
    for(var i = 0; i < styles.length; i++){
        if(styles[i].name == name){
            return styles[i];
        }
    }
}

function strEndsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function strEndsWithList(str, suffixes){
    for(var i = 0; i < suffixes.length; i++){
        if(strEndsWith(str, suffixes[i])){
            return true;
        }
    }
    return false;
}

function isEndOfLead(paragraph){
    if(paragraph.words.length < 2){
        return false;
    }
    var wordsUntilPoint = 0;
    for(; wordsUntilPoint < paragraph.words.length && !strEndsWithList(paragraph.words[paragraph.words.length - 1 - wordsUntilPoint].contents, END_OF_SENTENCE); wordsUntilPoint++);
    return wordsUntilPoint < 5;
}

function removeBlankParagraphs(story){
    for(var ap = story.paragraphs.length - 1; ap >= 0; ap--){
        if(story.paragraphs[ap].length < 2){
            story.paragraphs[ap].remove();
        }
    }
}
    

// layout the pages according to the given layout
// the layout to use
function layoutPages(layout){
    var document = app.activeDocument;
    for(var aa = 0; aa < layout.articles.length; aa++){
        var article = layout.articles[aa];
        if(article.status != 2){
            log("skip " + article.filename);
            continue;
        }
        log("layout " + article.filename);
        var startpage = document.pages[article.startpage - 1];
        overridePage(startpage);
        for(var aPage = article.startpage; aPage < article.startpage - 1 + article.pagecount; aPage++){
            var page = document.pages[aPage];
            overridePage(page);
            for(var j = 0; j < 2; j++){
                page.textFrames[0].remove();
            }
            page.textFrames[0].previousTextFrame = document.pages[aPage - 1].textFrames[MASTERSPREAD_INDEXES_LEFT.length - 1];
        }
    
        var oldInteractionPrefs = app.scriptPreferences.userInteractionLevel;
        app.scriptPreferences.userInteractionLevel = UserInteractionLevels.NEVER_INTERACT;
        startpage.textFrames[0].place(article.file);
        app.scriptPreferences.userInteractionLevel = oldInteractionPrefs;
        
        
        story = startpage.textFrames[0].parentStory;
        removeBlankParagraphs (story);
        story.paragraphs[0].appliedParagraphStyle = getParagraphStyleByName("Titel");
        var ap = 1;
        for(; ap < story.paragraphs.length && !isEndOfLead(story.paragraphs[ap]); ap++){
            story.paragraphs[ap].appliedParagraphStyle = getParagraphStyleByName("Leadtext");
        }
    
        if (ap < story.paragraphs.length){
            story.paragraphs[ap].appliedParagraphStyle = getParagraphStyleByName("Leadtext");
            for(var aw = story.paragraphs[ap].words.length - 1; aw >= 0 && !strEndsWithList(story.paragraphs[ap].words[aw].contents, END_OF_SENTENCE); aw--){
                story.paragraphs[ap].words[aw].appliedCharacterStyle = getCharacterStyleByName ("Autor");
            }
            ap++;
            
            for(; ap < story.paragraphs.length; ap++){
                story.paragraphs[ap].appliedParagraphStyle = getParagraphStyleByName("Fliesstext");   
            }
        } else {
            log(article.filename + " is missing a lead text");
        }
    }
}

function main(){
    initLog();
    var layout = readLayoutfile();
    layoutPages(layout);
}

app.doScript(main, ScriptLanguage.JAVASCRIPT, undefined, UndoModes.ENTIRE_SCRIPT, "Autolayout");