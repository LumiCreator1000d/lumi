// Compréhension locale des phrases (aucune connexion nécessaire)
const DAYS=["dimanche","lundi","mardi","mercredi","jeudi","vendredi","samedi"];
const NUM=["zéro","une","deux","trois","quatre","cinq","six","sept","huit","neuf","dix","onze","douze","treize","quatorze","quinze","seize","dix-sept","dix-huit","dix-neuf","vingt","vingt-et-une","vingt-deux","vingt-trois"];
function parseVoice(raw,now){
 now=now||new Date();
 let t=" "+raw.toLowerCase().replace(/[.,!?]/g," ").replace(/\s+/g," ")+" ";
 t=t.replace(/ midi /g," 12h ").replace(/ minuit /g," 0h ");
 NUM.forEach((w,i)=>{t=t.replace(new RegExp(" "+w+" heures?(?= )","g")," "+i+"h")});
 const type=/r[ée]veil/.test(t)?"a":(/ notes? |noter/.test(t)&&!/rappel/.test(t)?"n":"r");
 let h=null,mi=0,rel=null,m;
 if(m=t.match(/dans (\d+) (minute|heure)s?/))rel=+m[1]*(m[2]=="minute"?60000:3600000);
 else if(m=t.match(/(\d{1,2})\s*(?:heures?|h|:)\s*(\d{2})?/)){h=+m[1];mi=m[2]?+m[2]:0;
  if(/ et demie? /.test(t))mi=30;else if(/ et quart /.test(t))mi=15;else if(/ moins le quart /.test(t)){h-=1;mi=45}
  if(/ soir | après-midi /.test(t)&&h<12)h+=12}
 let d=new Date(now),said=false,wd=false;
 if(rel!=null)d=new Date(now.getTime()+rel);
 else{
  if(/ après-demain | apres-demain /.test(t)){d.setDate(d.getDate()+2);said=true}
  else if(/ demain /.test(t)){d.setDate(d.getDate()+1);said=true}
  else if(/ aujourd| ce soir | ce matin /.test(t))said=true;
  else DAYS.forEach((w,i)=>{if(t.includes(" "+w+" ")){d.setDate(d.getDate()+(i-now.getDay()+7)%7);said=wd=true}});
  if(/ ce soir /.test(t)&&h==null)h=20;
  d.setHours(h!=null?h:9,mi,0,0);
  if(wd&&d<=now)d.setDate(d.getDate()+7);else if(!said&&d<=now)d.setDate(d.getDate()+1);
 }
 let c=t.replace(/ (un|une|le|la|mon|ma) (?=rappel|réveil|reveil|note)/g," ")
  .replace(/\d{1,2}\s*(heures?|h|:)\s*\d{0,2}/g," ").replace(/dans \d+ (minutes?|heures?)/g," ")
    .replace(/ (et demie?|et quart|moins le quart|du matin|du soir|de l'après-midi|après-midi|ce soir|ce matin|après-demain|apres-demain|demain|aujourd'hui|aujourd’hui)(?= )/g," ")
  .replace(new RegExp(" ("+DAYS.join("|")+")(?= )","g")," ")
  .replace(/ (rappelle-moi|rappelle moi|réveille-moi|réveille moi|rappeler|rappel|réveil|reveil|notes?|noter|mets|met|mettre|ajoute|ajouter|crée|créer|programme|fais|peux-tu|pourrais-tu|s'il te plaît|s'il te plait|stp|lumi)(?= )/g," ")
  .replace(/ à(?= )/g," ").replace(/\s+/g," ").trim().replace(/^((de|d'|que|qu'|pour|à)\s*)+/,"");
 if(!c)c=type=="n"?raw.trim():"";
 c=c.charAt(0).toUpperCase()+c.slice(1);
 return{type,c,d,said,hasTime:h!=null||rel!=null};
}
if(typeof module!="undefined")module.exports=parseVoice;
