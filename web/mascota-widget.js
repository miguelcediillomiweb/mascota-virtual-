/* Mascota Virtual - widget flotante para cualquier página.
   Instala con: <script src="https://miguelcediillomiweb.github.io/mascota-virtual-/web/mascota-widget.js" defer></script>
*/
(()=>{'use strict';
if(window.__mascotaVirtualLoaded)return;window.__mascotaVirtualLoaded=true;
const css=`
#mv-pet{position:fixed;z-index:2147483000;left:15vw;top:65vh;width:92px;height:88px;border:0;padding:0;background:transparent;cursor:grab;touch-action:none;image-rendering:pixelated;filter:drop-shadow(0 5px 2px #0003);transform-origin:50% 85%;user-select:none}
#mv-pet svg{width:100%;height:100%;overflow:visible}
#mv-pet .mv-shadow{position:absolute;left:22px;bottom:-1px;width:48px;height:8px;border-radius:50%;background:#0003;z-index:-1}
#mv-pet.mv-walk svg{animation:mv-bob .32s infinite alternate}
#mv-pet.mv-run svg{animation:mv-run .14s infinite alternate}
#mv-pet.mv-sleep svg{animation:mv-sleep 1.8s infinite}
#mv-pet.mv-eat svg{animation:mv-peck .28s infinite alternate}
#mv-pet.mv-drink svg{animation:mv-drink .45s infinite alternate}
#mv-pet.mv-fall{animation:mv-fall .7s ease-in}
#mv-pet.mv-jump svg{animation:mv-jump .65s ease-in-out}
#mv-pet.mv-hang{transform:rotate(85deg)}
#mv-pet.mv-hide{opacity:0;pointer-events:none}
#mv-pet.mv-rocket{animation:mv-launch 3s ease-in forwards}
#mv-pet.mv-rocket:before{content:'🪖';position:absolute;top:-17px;left:30px;font-size:24px}
#mv-pet.mv-rocket:after{content:'🚀';position:absolute;left:18px;top:40px;font-size:42px;z-index:-1}
.mv-effect{position:fixed;z-index:2147482999;pointer-events:none;font-size:27px}
.mv-speech{position:absolute;top:-20px;left:50%;font:16px system-ui;white-space:nowrap;color:inherit;text-shadow:0 1px 3px #fff}
#mv-credit-run{position:fixed;z-index:2147483001;bottom:12px;left:0;display:flex;align-items:center;pointer-events:none;white-space:nowrap;filter:drop-shadow(0 3px 3px #0004);font-family:system-ui,sans-serif}
#mv-credit-run.mv-credit-ltr{animation:mv-credit-ltr var(--mv-duration) linear forwards}
#mv-credit-run.mv-credit-rtl{animation:mv-credit-rtl var(--mv-duration) linear forwards}
#mv-credit-run .mv-credit-pet{width:54px;height:52px;line-height:1;z-index:2}#mv-credit-run .mv-credit-pet svg{width:100%;height:100%;overflow:visible}
#mv-credit-run .mv-credit-ribbon{background:linear-gradient(180deg,#fff5c7,#f4d77a);color:#38250a;border:2px solid #7b4b17;padding:7px 13px;font-weight:800;font-size:14px;transform-origin:left center;animation:mv-credit-unroll .65s ease-out both}
#mv-credit-run.mv-credit-rtl .mv-credit-ribbon{transform-origin:right center}
@keyframes mv-credit-ltr{from{transform:translateX(-120%)}to{transform:translateX(105vw)}}
@keyframes mv-credit-rtl{from{transform:translateX(105vw)}to{transform:translateX(-120%)}}
@keyframes mv-credit-unroll{from{clip-path:inset(0 100% 0 0);opacity:.6}to{clip-path:inset(0 0 0 0);opacity:1}}
@keyframes mv-bob{to{transform:translateY(-4px) rotate(-2deg)}}@keyframes mv-run{to{transform:translateY(-7px) rotate(8deg)}}@keyframes mv-sleep{50%{transform:translateY(3px) scaleY(.94)}}@keyframes mv-peck{to{transform:rotate(15deg) translateY(5px)}}@keyframes mv-drink{to{transform:rotate(18deg) translateY(5px)}}@keyframes mv-fall{to{transform:rotate(95deg) translateY(50px)}}@keyframes mv-jump{50%{transform:translateY(-75px) rotate(-10deg)}to{transform:translateY(0)}}@keyframes mv-launch{to{transform:translate(90px,-110vh) rotate(35deg);opacity:0}}
`;
const style=document.createElement('style');style.textContent=css;document.head.appendChild(style);
const pet=document.createElement('button');pet.id='mv-pet';pet.type='button';pet.setAttribute('aria-label','Mascota virtual: tocar para despertarla o moverla');
pet.innerHTML=`<span class="mv-speech"></span><svg viewBox="0 0 128 112" aria-hidden="true"><g fill="#e99a58" stroke="#171522" stroke-width="4"><path d="M38 88Q27 101 38 104L57 104Q65 99 51 94Z"/><path d="M79 91Q70 103 83 105L102 102Q109 97 91 92Z"/></g><path d="M18 48Q18 16 55 18Q82 7 101 34Q119 48 110 76Q104 99 69 99L34 91Q11 82 18 48Z" fill="#f7ed8a" stroke="#171522" stroke-width="5"/><path d="M91 51Q111 38 122 51Q116 64 96 64Z" fill="#f3a66b" stroke="#171522" stroke-width="4"/><path d="M82 38Q91 26 104 38Q112 48 103 62L90 68L80 57Z" fill="#f5bd8e" stroke="#171522" stroke-width="4"/><ellipse cx="94" cy="51" rx="8" ry="11" fill="white" stroke="#171522" stroke-width="3"/><circle cx="97" cy="52" r="4" fill="#171522"/><path d="M38 31Q42 8 70 8L94 12L101 24L78 25L55 30Z" fill="#c14ee0" stroke="#171522" stroke-width="5"/><path d="M36 32L101 30L104 38L42 43Z" fill="#8b32bd" stroke="#171522" stroke-width="4"/></svg><span class="mv-shadow"></span>`;
document.body.appendChild(pet);
const speech=pet.querySelector('.mv-speech');let x=innerWidth*.15,y=innerHeight*.65,vx=1,mode='walk',busy=false,drag=false,lastX=0,lastY=0,oldTouch=0;
const clamp=(v,a,b)=>Math.max(a,Math.min(b,v));
function place(){x=clamp(x,0,innerWidth-100);y=clamp(y,24,innerHeight-100);pet.style.left=x+'px';pet.style.top=y+'px';pet.style.transform=vx<0?'scaleX(-1)':''}
function say(s){speech.textContent=s;setTimeout(()=>{if(speech.textContent===s)speech.textContent=''},1500)}
function setMode(s){mode=s;pet.className='mv-'+s}
function effect(s,emoji){const e=document.createElement('span');e.className='mv-effect';e.textContent=emoji;e.style.left=x+'px';e.style.top=(y+55)+'px';document.body.appendChild(e);setTimeout(()=>e.remove(),4200)}
const wait=t=>new Promise(r=>setTimeout(r,t));
let creditTimer=0;
function creatorFlyby(){
 if(document.hidden||document.getElementById('mv-credit-run'))return;
 const ltr=Math.random()<.5,fly=document.createElement('div');
 fly.id='mv-credit-run';fly.className=ltr?'mv-credit-ltr':'mv-credit-rtl';
 fly.style.setProperty('--mv-duration',Math.max(7,innerWidth/95)+'s');
 const runner=document.createElement('span');runner.className='mv-credit-pet';runner.innerHTML=pet.querySelector('svg').outerHTML;
 const ribbon=document.createElement('span');ribbon.className='mv-credit-ribbon';ribbon.textContent='Creado por Miguel Cedillo';
 if(ltr)fly.append(ribbon,runner);else fly.append(runner,ribbon);
 document.body.appendChild(fly);fly.addEventListener('animationend',()=>fly.remove(),{once:true});
}
function scheduleCredit(){creditTimer=setTimeout(()=>{creatorFlyby();scheduleCredit()},(7+Math.random()*8)*60*1000)}
scheduleCredit();
async function action(s){if(busy)return;busy=true;setMode(s);
if(s==='eat'){effect('grass','🌱');say('ñam ñam');}
if(s==='drink'){effect('puddle','💧');say('glu glu');}
if(s==='pee'){effect('pee','💦');say('¡Ahhh!');}
if(s==='sleep'){say('Z z z…');}
if(s==='hang'){say('¡Agárrame!');}
if(s==='rocket'){say('¡A la luna!');}
if(s==='hide'){setMode('hide');await wait(1700);x=Math.random()*(innerWidth-120);y=30+Math.random()*(innerHeight*.6);place();setMode('run');say('¡Volví!');await wait(1100);setMode('walk');busy=false;return;}
await wait(s==='sleep'?5200:s==='rocket'?2900: s==='hang'?1700: s==='fall'?850:1900);
if(s==='rocket'){x=Math.random()*(innerWidth-120);y=innerHeight*.65;place();say('¡Aterrizaje!');}
if(s==='hang'){setMode('fall');await wait(650);if(Math.random()<.5){setMode('jump');await wait(600)}}
if(s==='fall'&&Math.random()<.35){setMode('stunned');await wait(700)}
setMode('walk');busy=false;
}
function nearbyTarget(){const els=[...document.querySelectorAll('button,a,[role="button"],input,header,nav')].filter(el=>{const r=el.getBoundingClientRect();return r.width>35&&r.height>15&&r.bottom>0&&r.top<innerHeight&&r.right>0&&r.left<innerWidth;});if(!els.length)return null;return els[Math.floor(Math.random()*els.length)].getBoundingClientRect()}
async function wander(){if(busy||drag)return;const r=Math.random();
if(r<.30){vx=Math.random()<.5?-1:1;setMode('walk');x+=vx*(45+Math.random()*130);y+=Math.random()*50-25;}
else if(r<.42){vx=Math.random()<.5?-1:1;setMode('run');x+=vx*(130+Math.random()*180);await wait(650);setMode('walk')}
else if(r<.51){setMode('jump');await wait(650);setMode('walk')}
else if(r<.59){await action('eat')}
else if(r<.66){await action('drink')}
else if(r<.70){await action('pee')}
else if(r<.77){await action('sleep')}
else if(r<.84){const t=nearbyTarget();if(t){x=clamp(t.left,0,innerWidth-100);y=clamp(t.top-65,25,innerHeight-100);place();await action('hang')}else await action('fall')}
else if(r<.91){await action('fall')}
else if(r<.96){await action('hide')}
else await action('rocket');
place();if(!busy)setTimeout(wander,900+Math.random()*2300)
}
pet.addEventListener('pointerdown',e=>{drag=true;lastX=e.clientX;lastY=e.clientY;pet.setPointerCapture(e.pointerId);});
pet.addEventListener('pointermove',e=>{if(!drag)return;x+=e.clientX-lastX;y+=e.clientY-lastY;lastX=e.clientX;lastY=e.clientY;place()});
pet.addEventListener('pointerup',()=>{drag=false;if(mode==='sleep'){setMode('jump');say('¡Desperté!');setTimeout(()=>setMode('walk'),650)}else{setMode('walk');say('¡Hola!')}});
document.addEventListener('visibilitychange',()=>{if(!document.hidden&&!busy)wander()});
addEventListener('resize',place);place();setTimeout(wander,1800);
})();