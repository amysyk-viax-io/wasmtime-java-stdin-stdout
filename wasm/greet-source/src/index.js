import * as fs from "javy/fs";

const inputData = fs.readFileSync(fs.STDIO.Stdin);
const inputStr= new TextDecoder("utf-8").decode(inputData);
const inputObj = JSON.parse(inputStr);

const outputObj = { greet: `Hello ${inputObj.name}` };

const outputStr = JSON.stringify(outputObj);
const outputData = new TextEncoder().encode(outputStr);
fs.writeFileSync(fs.STDIO.Stdout, outputData);
