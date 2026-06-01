// This script runs in the VS Code Markdown preview environment
const PLANTUML_LANGUAGES = ['plantuml', 'puml'];

// The main PlantUML server URL, or you can use local if needed
const PLANTUML_SERVER_URL = 'https://www.plantuml.com/plantuml';

document.addEventListener('DOMContentLoaded', () => {
  renderPlantUmlCodeBlocks();
});

function renderPlantUmlCodeBlocks() {
  const codeBlocks = document.querySelectorAll('pre code');
  
  codeBlocks.forEach(codeBlock => {
    let isPlantUml = false;
    
    for (const lang of PLANTUML_LANGUAGES) {
      if (codeBlock.classList.contains(`language-${lang}`)) {
        isPlantUml = true;
        break;
      }
    }
    
    if (isPlantUml) {
      try {
        const code = codeBlock.textContent || '';
        const encoded = encodePlantUml(code);
        const img = document.createElement('img');
        img.src = `${PLANTUML_SERVER_URL}/svg/${encoded}`;
        img.alt = 'PlantUML Diagram';
        
        const pre = codeBlock.parentElement;
        if (pre) {
          const parent = pre.parentElement;
          if (parent) {
            parent.replaceChild(img, pre);
          }
        }
      } catch (e) {
        console.error('Failed to render PlantUML diagram', e);
      }
    }
  });
}

function encodePlantUml(source: string): string {
  // Simple implementation for now - in real code, you'd use plantuml-encoder npm package
  let processed = source;
  if (!processed.trim().startsWith('@start')) {
    processed = '@startuml\n' + processed + '\n@enduml';
  }
  
  // Add Smetana layout engine pragma
  if (!processed.includes('!pragma layout smetana')) {
    if (processed.startsWith('@startuml')) {
      processed = processed.replace('@startuml', '@startuml\n!pragma layout smetana');
    }
  }
  
  // Simple UTF-8 to Base64 encoder (simplified version)
  try {
    return btoa(unescape(encodeURIComponent(processed)));
  } catch (e) {
    console.error('Failed to encode PlantUML source', e);
    return btoa(source);
  }
}
