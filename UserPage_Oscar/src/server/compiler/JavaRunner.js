const { spawn } = require('child_process');
const Runner = require('./Runner');

class JavaRunner extends Runner {
  constructor(questionId) {
    super();
    this.defaultfile = `Question${questionId}.java`; // 使用 questionId 构建默认文件名
    this.questionId = questionId;
  }

  defaultFile() {
    return this.defaultfile;
  }

  run(file, directory, filename, extension, callback) {
    if (extension.toLowerCase() !== '.java') {
      console.log(`${file} is not a java file.`);
    }
    this.compile(file, directory, filename, callback);
  }

  // compile java source file
  compile(file, directory, filename, callback) {
    // set working directory for child_process
    const options = { cwd: directory };
    const argsCompile = [file];
    const compiler = spawn('javac', argsCompile);
    compiler.stdout.on('data', (data) => {
      console.log(`stdout: ${data}`);
    });
    compiler.stderr.on('data', (data) => {
      console.log(`JavaRunner 36 stderr: ${String(data)}`);
      callback('1', String(data)); // 1, compile error
    });
    compiler.on('close', (data) => {
      if (data === 0) {
        this.execute(filename, options, callback);
      }
    });
  }

  // execute the compiled class file
  execute(filename, options, callback) {
    const argsRun = [filename];
    const executor = spawn('java', argsRun, options);
    executor.stdout.on('data', (output) => {
      console.log(String(output));
      callback('0', String(output)); // 0, no error
    });
    executor.stderr.on('data', (output) => {
      console.log(`JavaRunner 55 stderr: ${String(output)}`);
      callback('2', String(output)); // 2, execution failure
    });
    executor.on('close', (output) => {
      this.log(`stdout: ${output}`);

   
    });
  }

  log(message) {
    console.log(message);
  }
}

module.exports = JavaRunner;