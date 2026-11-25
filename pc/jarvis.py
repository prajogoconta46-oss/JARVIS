import os
import platform
import subprocess
import tkinter as tk
import webbrowser
import threading
from datetime import datetime
import psutil

try:
    import pyttsx3
except Exception:
    pyttsx3 = None

BG, PANEL, FG, ACCENT = "#07111f", "#101e31", "#e7f0ff", "#5eead4"

class JarvisApp:
    def __init__(self, root):
        self.root=root
        root.title("JARVIS | Assistente pessoal")
        root.geometry("850x650"); root.minsize(650,480); root.configure(bg=BG)
        self.engine=None
        if pyttsx3:
            try:
                self.engine=pyttsx3.init()
                self.engine.setProperty("rate",175)
            except Exception: pass
        header=tk.Frame(root,bg=BG,padx=24,pady=18); header.pack(fill="x")
        tk.Label(header,text="JARVIS",fg=ACCENT,bg=BG,font=("Segoe UI",28,"bold")).pack(anchor="w")
        tk.Label(header,text="Assistente para Windows • converse comigo",fg=FG,bg=BG,font=("Segoe UI",11)).pack(anchor="w")
        self.chat=tk.Text(root,bg=PANEL,fg=FG,insertbackground="white",font=("Segoe UI",11),relief="flat",wrap="word",padx=15,pady=12,state="disabled")
        self.chat.pack(fill="both",expand=True,padx=24,pady=(4,12))
        bottom=tk.Frame(root,bg=BG,padx=24,pady=12); bottom.pack(fill="x")
        self.entry=tk.Entry(bottom,bg="#17263b",fg=FG,insertbackground="white",relief="flat",font=("Segoe UI",12))
        self.entry.pack(fill="x",ipady=12); self.entry.bind("<Return>",self.send)
        self.status=tk.Label(root,text="Online • digite e pressione Enter",bg=BG,fg=ACCENT,font=("Segoe UI",10))
        self.status.pack(anchor="w",padx=24,pady=(0,12)); self.entry.focus_set()
        self.add("JARVIS","Olá! Digite sua mensagem e pressione Enter. Posso abrir alguns programas, consultar informações do PC e pesquisar na web.")
    def add(self,who,msg):
        self.chat.configure(state="normal"); self.chat.insert("end",f"{who}: {msg}\n\n"); self.chat.see("end"); self.chat.configure(state="disabled")
    def speak(self,msg):
        if not self.engine: return
        def worker():
            try: self.engine.say(msg); self.engine.runAndWait()
            except Exception: pass
        threading.Thread(target=worker,daemon=True).start()
    def send(self,event=None):
        msg=self.entry.get().strip()
        if not msg:return
        self.entry.delete(0,"end"); self.add("Você",msg); self.status.config(text="Respondendo...")
        self.root.after(50,lambda:self.respond(msg))
    def diagnose(self):
        cpu=psutil.cpu_percent(interval=.3); mem=psutil.virtual_memory()
        drive=os.environ.get("SystemDrive","C:")+"\\"
        disk=psutil.disk_usage(drive)
        return f"CPU: {cpu}%. RAM: {mem.percent}% usada, {round(mem.available/1024**3,1)} GB disponível. Disco: {disk.percent}% ocupado, {round(disk.free/1024**3,1)} GB livres. Sistema: {platform.system()} {platform.release()}."
    def respond(self,msg):
        q=msg.lower(); answer=""
        if any(x in q for x in ["diagnóstico","diagnostico","desempenho","status do pc","como está meu pc"]): answer=self.diagnose()
        elif any(x in q for x in ["abre bloco de notas","notepad"]): subprocess.Popen(["notepad.exe"]); answer="Abrindo o Bloco de Notas."
        elif any(x in q for x in ["abre calculadora","calculadora"]): subprocess.Popen(["calc.exe"]); answer="Abrindo a calculadora."
        elif any(x in q for x in ["abre explorador","explorador de arquivos"]): subprocess.Popen(["explorer.exe"]); answer="Abrindo o Explorador de Arquivos."
        elif "youtube" in q: webbrowser.open("https://youtube.com"); answer="Abrindo YouTube."
        elif any(x in q for x in ["pesquisa na internet","pesquisar na internet","pesquise"]):
            term=q.replace("pesquisa na internet","").replace("pesquisar na internet","").replace("pesquise","").strip()
            webbrowser.open("https://www.google.com/search?q="+term.replace(" ","+")); answer="Pesquisando na internet: "+term
        elif "google" in q: webbrowser.open("https://google.com"); answer="Abrindo o Google."
        elif any(x in q for x in ["que horas","hora agora","horário"]): answer="Agora são "+datetime.now().strftime("%H:%M")+"."
        elif any(x in q for x in ["que dia","data de hoje"]): answer="Hoje é "+datetime.now().strftime("%d/%m/%Y")+"."
        elif any(x in q for x in ["olá","ola","oi","bom dia","boa tarde","boa noite"]): answer="Olá! Estou aqui. O que você gostaria que eu fizesse?"
        elif any(x in q for x in ["obrigado","obrigada","valeu"]): answer="Por nada! Conte comigo."
        elif any(x in q for x in ["fechar jarvis","encerrar assistente"]):
            answer="Até logo!"; self.add("JARVIS",answer); self.speak(answer); self.root.after(700,self.root.destroy); return
        else: answer="Ainda sou um assistente inicial, sem IA generativa conectada. Posso atender comandos como diagnóstico do PC, abrir calculadora, Bloco de Notas, YouTube, pesquisar na internet, informar hora e data."
        self.add("JARVIS",answer); self.status.config(text="Online • digite e pressione Enter"); self.speak(answer)

if __name__=="__main__":
    root=tk.Tk(); JarvisApp(root); root.mainloop()
