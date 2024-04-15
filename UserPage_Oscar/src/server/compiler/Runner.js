const { exec } = require('child_process');

module.exports = class JavaRunner {
  run(file, directory, callback) {
    exec(`javac ${file}`, (error, stdout, stderr) => {
      if (error) {
        console.error(`Compilation failed: ${stderr}`);
        callback('error', 'Compilation failed');
      } else {
        const className = file.split('/').pop().split('.').shift();
        exec(`java -classpath ${directory} ${className}`, (error, stdout, stderr) => {
          if (error) {
            console.error(`Execution failed: ${stderr}`);
            callback('error', 'Execution failed');
          } else {
            console.log('Execution successful');
            console.log(`Output: ${stdout}`);
            callback('success', 'Execution successful');
          }
        });
      }
    });
  }

  defaultFile() {
    return 'Question3.java';
  }
};
