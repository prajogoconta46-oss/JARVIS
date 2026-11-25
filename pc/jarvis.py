import os
import platform
import subprocess
import tkinter as tk
import webbrowser
from tkinter import ttk, messagebox
import psutil

BG = "#0b1220"
PANEL = "#101b2e"
FG = "#e5edf9"
ACCENT = "#67e8f9"

class JarvisApp:
    def __init__(self, root):
        self.root = root
        root.title("JARVIS | Assistente pessoal")
        root.geometry("920x640")
        root.minsize(720, 500)
        root.configure(bg=BG)
        style = ttk.Style()
        style.theme_use("clam")
        style.configure("TFrame", background=BG)
        style.configure("TLabel", background=BG, foreground=FG, font=("Segoe UI", 11))
        style.configure("Header.TLabel", font=("Segoe UI", 26, "bold"), foreground=ACCENT)
        style.configure("TButton", font=("Segoe UI", 11), padding=9)
        frame = ttk.Frame(root, padding=22)
        frame.pack(fill="both", expand=True)
        ttk.Label(frame, text="JARVIS", style="Header.TLabel").pack(anchor="w")
        ttk.Label(frame, text="Assistente inteligente • Windows", font=("Segoe UI", 11)).pack(anchor="w", pady=(0, 16))
        bar = ttk.Frame(frame); bar.pack(fill="x")
        buttons = [("Diagnóstico", self.diagnose), ("Notepad", lambda: self.open_app(["notepad.exe"])),
                   ("Calculadora", lambda: self.open_app(["calc.exe"])), ("Explorador", lambda: self.open_app(["explorer.exe"])),
                   ("Web", lambda: webbrowser.open("https://www.google.com"))]
        for label, fn in buttons:
            ttk.Button(bar, text=label, command=fn).pack(side="left", padx=4, pady=4)
        self.output = tk.Text(frame, bg=PANEL, fg="#dbeafe", insertbackground="white",
                             font=("Consolas", 11), relief="flat", wrap="word")
        self.output.pack(fill="both", expand=True, pady=14)
        self.output.insert("end", "JARVIS pronto.\nUse os botões para executar ferramentas locais.\n")
        self.status = tk.StringVar(value="Pronto")
        ttk.Label(frame, textvariable=self.status).pack(anchor="w")
    def report(self, text):
        self.output.delete("1.0", "end")
        self.output.insert("end", text)
        self.status.set("Concluído")
    def diagnose(self):
        try:
            cpu = psutil.cpu_percent(interval=0.6)
            mem = psutil.virtual_memory()
            disk = psutil.disk_usage(os.environ.get("SystemDrive", "C:") + "\\")
            lines = ["DIAGNÓSTICO", "Sistema: " + platform.system() + " " + platform.release(),
                     "PC: " + platform.node(), "CPU: " + str(cpu) + "%",
                     "RAM: " + str(mem.percent) + "% usada (" + str(round(mem.used / 1024**3, 1)) + " / " + str(round(mem.total / 1024**3, 1)) + " GB)",
                     "Disco: " + str(disk.percent) + "% usado; " + str(round(disk.free / 1024**3, 1)) + " GB livres"]
            self.report("\n".join(lines))
        except Exception as exc:
            messagebox.showerror("Diagnóstico", "Não foi possível concluir: " + str(exc))
    def open_app(self, command):
        try:
            subprocess.Popen(command, shell=False)
            self.status.set("Programa iniciado.")
        except Exception as exc:
            messagebox.showerror("Erro ao abrir", str(exc))

if __name__ == "__main__":
    root = tk.Tk()
    JarvisApp(root)
    root.mainloop()
