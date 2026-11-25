import os
import platform
import subprocess
import tkinter as tk
import webbrowser
import threading
from datetime import datetime
from urllib.parse import quote_plus
import psutil

try:
    import pyttsx3
except Exception:
    pyttsx3 = None

BG, PANEL, FG, ACCENT = "#07111f", "#101e31", "#e7f0ff", "#5eead4"

class JarvisApp:
    def __init__(self, root):
        self.root = root
        root.title("JARVIS | Assistente pessoal")
        root.geometry("900x680")
        root.minsize(650, 480)
        root.configure(bg=BG)
        self.engine = None
        if pyttsx3:
            try:
                self.engine = pyttsx3.init()
                self.engine.setProperty("rate", 175)
            except Exception:
                pass
        header = tk.Frame(root, bg=BG, padx=24, pady=18)
        header.pack(fill="x")
        tk.Label(header, text="JARVIS", fg=ACCENT, bg=BG, font=("Segoe UI", 28, "bold")).pack(anchor="w")
        tk.Label(header, text="Assistente pessoal para Windows • chat e voz", fg=FG, bg=BG, font=("Segoe UI", 11)).pack(anchor="w")
        self.chat = tk.Text(root, bg=PANEL, fg=FG, insertbackground="white", font=("Segoe UI", 11), relief="flat", wrap="word", padx=15, pady=12, state="disabled")
        self.chat.pack(fill="both", expand=True, padx=24, pady=(4, 12))
        bottom = tk.Frame(root, bg=BG, padx=24, pady=12)
        bottom.pack(fill="x")
        self.entry = tk.Entry(bottom, bg="#17263b", fg=FG, insertbackground="white", relief="flat", font=("Segoe UI", 12))
        self.entry.pack(fill="x", ipady=12)
        self.entry.bind("<Return>", self.send)
        self.status = tk.Label(root, text="Pronto • digite e pressione Enter", bg=BG, fg=ACCENT, font=("Segoe UI", 10))
        self.status.pack(anchor="w", padx=24, pady=(0, 12))
        self.entry.focus_set()
        self.add("JARVIS", "Olá! Pode conversar comigo digitando. Posso verificar o PC, abrir programas, pesquisar na internet, mostrar processos e falar minhas respostas. Ainda não sou uma IA generativa conectada.")
    def add(self, who, msg):
        self.chat.configure(state="normal")
        self.chat.insert("end", f"{who}: {msg}\n\n")
        self.chat.see("end")
        self.chat.configure(state="disabled")
    def speak(self, msg):
        if not self.engine: return
        def worker():
            try:
                self.engine.say(msg)
                self.engine.runAndWait()
            except Exception: pass
        threading.Thread(target=worker, daemon=True).start()
    def send(self, event=None):
        msg = self.entry.get().strip()
        if not msg: return
        self.entry.delete(0, "end")
        self.add("Você", msg)
        self.status.config(text="Respondendo...")
        self.root.after(30, lambda: self.respond(msg))
    def diagnose(self):
        cpu = psutil.cpu_percent(interval=.4)
        mem = psutil.virtual_memory()
        drive = os.environ.get("SystemDrive", "C:") + "\\"
        disk = psutil.disk_usage(drive)
        battery = psutil.sensors_battery()
        bat = f" Bateria: {battery.percent}%." if battery else " Bateria não detectada (PC pode estar conectado à tomada)."
        return f"Resumo do computador: CPU {cpu}%. RAM {mem.percent}% usada ({round(mem.available/1024**3,1)} GB disponível). Disco {disk.percent}% ocupado, {round(disk.free/1024**3,1)} GB livres. Windows {platform.release()}.{bat}"
    def processes(self):
        items=[]
        for p in psutil.process_iter(["name","cpu_percent","memory_info"]):
            try:
                info=p.info
                mem=info["memory_info"].rss/1024**2 if info["memory_info"] else 0
                items.append((info["cpu_percent"] or 0,mem,info["name"] or "processo"))
            except (psutil.NoSuchProcess,psutil.AccessDenied): pass
        items.sort(reverse=True)
        return "Processos com maior uso de memória (não encerrei nenhum):\n" + "\n".join(f"• {n}: {m:.0f} MB" for c,m,n in items[:8])
    def open_downloads(self):
        folder=os.path.join(os.path.expanduser("~"),"Downloads")
        if os.path.isdir(folder): os.startfile(folder); return "Abrindo sua pasta Downloads."
        return "Não encontrei a pasta Downloads."
    def respond(self, msg):
        q=msg.lower().strip(); answer=""
        try:
            if any(x in q for x in ["diagnóstico","diagnostico","desempenho","status do pc","como está meu pc","informações do pc","informacoes do pc"]): answer=self.diagnose()
            elif any(x in q for x in ["processos","programas em execução","programas rodando"]): answer=self.processes()
            elif any(x in q for x in ["abre bloco de notas","notepad"]): subprocess.Popen(["notepad.exe"]); answer="Abrindo o Bloco de Notas."
            elif any(x in q for x in ["abre calculadora","calculadora"]): subprocess.Popen(["calc.exe"]); answer="Abrindo a calculadora."
            elif any(x in q for x in ["abre explorador","explorador de arquivos"]): subprocess.Popen(["explorer.exe"]); answer="Abrindo o Explorador de Arquivos."
            elif any(x in q for x in ["downloads","pasta de download"]): answer=self.open_downloads()
            elif any(x in q for x in ["configurações","configuracoes","settings"]): subprocess.Popen(["cmd","/c","start","ms-settings:"]); answer="Abrindo as Configurações do Windows."
            elif "youtube" in q: webbrowser.open("https://youtube.com"); answer="Abrindo YouTube."
            elif any(x in q for x in ["pesquisa na internet","pesquisar na internet","pesquise","procure na web"]):
                term=q.replace("pesquisa na internet","").replace("pesquisar na internet","").replace("pesquise","").replace("procure na web","").strip()
                if not term: term="informações gerais"
                webbrowser.open("https://www.google.com/search?q="+quote_plus(term)); answer="Pesquisando na web por: "+term
            elif "google" in q: webbrowser.open("https://google.com"); answer="Abrindo Google."
            elif any(x in q for x in ["que horas","hora agora","horário"]): answer="Agora são "+datetime.now().strftime("%H:%M")+"."
            elif any(x in q for x in ["que dia","data de hoje","qual a data"]): answer="Hoje é "+datetime.now().strftime("%d/%m/%Y")+"."
            elif any(x in q for x in ["olá","ola","oi","bom dia","boa tarde","boa noite"]): answer="Olá! Estou pronto. Como posso ajudar?"
            elif any(x in q for x in ["obrigado","obrigada","valeu"]): answer="Por nada! Conte comigo."
            elif any(x in q for x in ["fechar jarvis","encerrar assistente","sair do jarvis"]):
                answer="Até logo!"; self.add("JARVIS",answer); self.speak(answer); self.root.after(700,self.root.destroy); return
            else: answer="Não tenho uma IA generativa conectada ainda, mas posso atender: diagnóstico do PC, processos, abrir calculadora, Notepad, Explorador, Downloads, Configurações, YouTube, pesquisa na web, hora e data."
        except Exception as exc:
            answer=f"Não consegui executar essa tarefa. Detalhe: {exc}"
        self.add("JARVIS",answer)
        self.status.config(text="Pronto • digite e pressione Enter")
        self.speak(answer)

if __name__ == "__main__":
    root = tk.Tk()
    JarvisApp(root)
    root.mainloop()
