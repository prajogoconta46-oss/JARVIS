import os, platform, subprocess, tkinter as tk
from tkinter import ttk, messagebox
import psutil

class JarvisApp:
    def __init__(self, root):
        self.root=root
        root.title("JARVIS | Assistente pessoal")
        root.geometry("850x560"); root.minsize(680,440); root.configure(bg="#0b1220")
        style=ttk.Style(); style.theme_use("clam")
        style.configure("TFrame",background="#0b1220")
        style.configure("TLabel",background="#0b1220",foreground="#e5edf9",font=("Segoe UI",11))
        style.configure("Header.TLabel",font=("Segoe UI",24,"bold"),foreground="#67e8f9")
        style.configure("TButton",font=("Segoe UI",11),padding=9)
        frame=ttk.Frame(root,padding=22); frame.pack(fill="both",expand=True)
        ttk.Label(frame,text="JARVIS",style="Header.TLabel").pack(anchor="w")
        ttk.Label(frame,text="Assistente inteligente • Windows").pack(anchor="w",pady=(0,14))
        bar=ttk.Frame(frame); bar.pack(fill="x")
        for label,fn in [("Diagnóstico",self.diagnose),("Notepad",lambda:self.open_app("notepad")),("Calculadora",lambda:self.open_app("calc")),("Explorador",lambda:self.open_app("explorer")),("Navegador",self.open_browser)]:
            ttk.Button(bar,text=label,command=fn).pack(side="left",padx=3,pady=4)
        self.output=tk.Text(frame,bg="#101b2e",fg="#dbeafe",insertbackground="white",font=("Consolas",11),relief="flat",wrap="word")
        self.output.pack(fill="both",expand=True,pady=14)
        self.output.insert("end","JARVIS pronto. Selecione uma ferramenta.\nEste protótipo não executa comandos arbitrários.\n")
        self.status=tk.StringVar(value="Pronto")
        ttk.Label(frame,textvariable=self.status).pack(anchor="w")
    def report(self,s):
        self.output.delete("1.0","end"); self.output.insert("end",s); self.status.set("Concluído")
    def diagnose(self):
        try:
            cpu=psutil.cpu_percent(interval=.5); mem=psutil.virtual_memory(); disk=psutil.disk_usage(os.path.abspath(os.sep))
            self.report("Diagnóstico\nSistema: "+platform.system()+" "+platform.release()+"\nPC: "+platform.node()+"\nCPU: "+str(cpu)+"%\nRAM: "+str(mem.percent)+"% usada\nDisco: "+str(disk.percent)+"% usado; "+str(disk.free//(1024**3))+" GB livres")
        except Exception as e: messagebox.showerror("Diagnóstico",str(e))
    def open_app(self,app):
        try: subprocess.Popen(app,shell=False); self.status.set("Abrindo "+app)
        except Exception as e: messagebox.showerror("Erro",str(e))
    def open_browser(self):
        try: os.startfile("https://www.google.com")
        except Exception as e: messagebox.showerror("Erro",str(e))

if __name__=="__main__":
    root=tk.Tk(); JarvisApp(root); root.mainloop()
