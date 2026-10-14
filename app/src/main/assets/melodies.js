const M={
 "Berceuse des nuages":{n:[523,659,784,659,587,698,880,698],s:1000},
 "Pluie de lune":{n:[392,523,587,784,659,880,587,523],s:1200,rnd:1},
 "Aube dorée":{n:[261,329,392,523,392,329,440,523,659,523],s:1100}
};
let ac,tm,mg;
function ctx(){if(!ac)ac=new(window.AudioContext||window.webkitAudioContext)();if(ac.state=="suspended")ac.resume();return ac}
function tone(f){const a=ctx(),t=a.currentTime;
 [[f,.12],[f*2,.03]].forEach(([fr,v])=>{const o=a.createOscillator(),g=a.createGain();o.type="sine";o.frequency.value=fr;
  g.gain.setValueAtTime(0,t);g.gain.linearRampToValueAtTime(v,t+.5);g.gain.exponentialRampToValueAtTime(.001,t+3);
  o.connect(g);g.connect(mg||a.destination);o.start(t);o.stop(t+3.1)})}
function play(k,fade){stop();const a=ctx();mg=a.createGain();mg.connect(a.destination);const t=a.currentTime;
 if(fade){mg.gain.setValueAtTime(.25,t);mg.gain.linearRampToValueAtTime(1,t+fade)}
 const m=M[k]||M["Berceuse des nuages"];let i=0;
 const step=()=>{tone(m.n[m.rnd?Math.random()*m.n.length|0:i%m.n.length]);i++};step();tm=setInterval(step,m.s)}
function stop(){clearInterval(tm);tm=null}
