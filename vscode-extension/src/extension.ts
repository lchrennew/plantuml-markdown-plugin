// The main VS Code extension file
import * as vscode from 'vscode';

export function activate(context: vscode.ExtensionContext) {
  console.log('PlantUML Markdown Preview extension is now active!');
  
  // The main rendering logic is in prerender.ts, which runs in the Markdown preview
}

export function deactivate() {
  console.log('PlantUML Markdown Preview extension is now deactivated!');
}
