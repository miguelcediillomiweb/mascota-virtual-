(()=>{'use strict';
const pet=document.querySelector('#pet'),stage=document.querySelector('#stage'),effects=document.querySelector('#effects'),status=document.querySelector('#status'),detail=document.querySelector('#detail'),speech=document.querySelector('#speech');
let x=40,y=68,dir=1,state='walk',paused=false,drag=false,dragged=false,lastX=0,lastY=0,cycle=0,timer=null,moveTimer=null;
const labels={walk:['Explorando','Está curioseando por aquí.'],run:['¡A toda velocidad!','Le dio un ataque de energía.'],sleep:['Dormitando','Un ratito de descanso.'],eat:['Hora de comer','Encontró un poco de pasto fresco.'],drink:['Tomando agua','Un charquito apareció de la nada.'],pee:['Una pausa','Marcando territorio como caricatura.'],hang:['¡Se está colgando!','Se agarró del borde.'],fall:['¡Ups!','Perdió el equilibrio.'],stunned:['Aturdida','Se sacude y vuelve a intentarlo.'],jump:['¡Brinco!','Está llena de energía.'],rocket:['¡Rumbo a la luna!','¡Casco puesto y despegue!'],vanish:['¿Dónde está?','Se esfumó… ¿por dónde volverá?']};
function render(s,msg){state=s;pet.className='pet '+s;status.textContent=labels[s]?.[0]||s;detail.textContent=msg||labels[s]?.[1]||''}
function place(){pet.style.left=x+'%';pet.style.top=y+'%';pet.style.transform=dir<0?'scaleX(-1)':''}
function say(t){speech.textContent=t||'';if(t)setTimeout(()=>{if(speech.textContent===t)speech.textContent=''},1700)}
function effect(kind){const el=document.createElement('span');el.className='effect '+kind;el.style.setProperty('--x',Math.max(2,Math.min(88,x))+'%');if(kind==='grass')el.textContent='🌱';effects.append(el);setTimeout(()=>el.remove(),5200)}
function moveTo(nx,ny,speed=1){x=Math.max(2,Math.min(88,nx));y=Math.max(30,Math.min(78,ny));place()}
function wait(ms){return new Promise(r=>setTimeout(r,ms))}
async function act(s,duration=1800){if(paused||drag)return;clearTimeout(timer);render(s);if(s==='eat'){effect('grass');say('ñam ñam');}if(s==='drink'){effect('puddle');say('glu glu');}if(s==='pee'){effect('pee');say('¡Ahhh!');}if(s==='rocket'){say('¡Despegando!');const tr=document.createElement('span');tr.className='effect rocket-trail';tr.textContent='🚀';effects.append(tr);setTimeout(()=>tr.remove(),3000)}
if(s==='vanish'){await wait(600);moveTo(Math.random()*75+5,Math.random()*40+30);await wait(1700);render('walk');say('¡Aquí estoy!');schedule();return}
if(s==='rocket'){await wait(3100);moveTo(Math.random()*70+10,35+Math.random()*35);render('walk');say('¡Regresé de la luna!');schedule();return}
if(s==='fall'){await wait(650);render('stunned');await wait(900);render('walk');schedule();return}
await wait(duration);if(s==='hang'){render('fall');await wait(650);render('stunned');await wait(900)}render('walk');schedule()}
async function wander(){if(paused||drag)return;const r=Math.random();if(r<.23){dir=Math.random()<.5?-1:1;render('walk');moveTo(x+dir*(5+Math.random()*18),y+(Math.random()*8-4));}else if(r<.34){render('run');dir=Math.random()<.5?-1:1;moveTo(x+dir*(18+Math.random()*24),y);await wait(900)}else if(r<.44){render('jump');await wait(650)}else if(r<.53){await act('eat',2600);return}else if(r<.61){await act('drink',2300);return}else if(r<.67){await act('pee',1700);return}else if(r<.74){await act('hang',1700);return}else if(r<.80){await act('fall',1000);return}else if(r<.85){await act('vanish');return}else if(r<.89){await act('rocket');return}else if(r<.95){await act('sleep',4500);return}else{say('…');}place();schedule()}
function schedule(){clearTimeout(timer);if(paused||drag)return;timer=setTimeout(wander,1300+Math.random()*3300)}
function wake(){if(state==='sleep'){render('jump');say('¡Ya desperté!');setTimeout(()=>{render('walk');schedule()},700)}else if(state==='vanish'){render('walk');schedule()}else{say(['¿Qué pasó?','¡Hola!','¡Vamos!'][Math.floor(Math.random()*3)]);}}
pet.addEventListener('pointerdown',e=>{drag=true;dragged=false;lastX=e.clientX;lastY=e.clientY;pet.setPointerCapture(e.pointerId);clearTimeout(timer);});
pet.addEventListener('pointermove',e=>{if(!drag)return;const dx=e.clientX-lastX,dy=e.clientY-lastY;if(Math.abs(dx)+Math.abs(dy)>4)dragged=true;const r=stage.getBoundingClientRect();x=Math.max(0,Math.min(88,x+dx/r.width*100));y=Math.max(20,Math.min(80,y+dy/r.height*100));lastX=e.clientX;lastY=e.clientY;place()});
pet.addEventListener('pointerup',()=>{drag=false;if(!dragged)wake();else{render('walk');schedule()}});
document.querySelector('#pauseBtn').onclick=()=>{paused=!paused;document.querySelector('#pauseBtn').textContent=paused?'Continuar':'Pausar';if(paused){clearTimeout(timer);render('sleep','La mascota está en pausa.')}else{render('walk');schedule()}};
document.querySelector('#resetBtn').onclick=()=>{paused=false;document.querySelector('#pauseBtn').textContent='Pausar';wake();render('walk');schedule()};
document.querySelectorAll('[data-action]').forEach(b=>b.onclick=()=>{paused=false;document.querySelector('#pauseBtn').textContent='Pausar';act(b.dataset.action==='rocket'?'rocket':b.dataset.action,b.dataset.action==='sleep'?6000:2200)});
document.addEventListener('visibilitychange',()=>{if(!document.hidden&&!paused&&state==='sleep')wake()});
place();schedule();
})();