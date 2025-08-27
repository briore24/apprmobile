/*
 * Licensed Materials - Property of IBM
 *
 * 5724-U18, 5737-M66
 *
 * (C) Copyright IBM Corp. 2022,2023 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 */


const { fileList, folderCopy, folderDelete } = require('./copyTechFilesList');
const fs = require('fs');
const path = require('path');
const shell = require('shelljs');

function copyFileFolder() {
    fs.mkdir(`${__dirname}/../SharedResources/Technician/`,()=> {})
    // below code will iterate over filesArray and will copy inside SharedResources/Technician
    fileList.forEach(fileName => {
        fs.copyFile(path.normalize(__dirname) + fileName, path.normalize(`${__dirname}/../SharedResources/Technician/${path.parse(fileName).base}`), (err) => {
            if (err) throw err;
            console.log(`${path.parse(fileName).base} fiile was copied`);
        });
    });

    // below code will iterate over folderArry and will copy folder with all the file it have inside SharedResources/Technician
    folderCopy.forEach(folderSrc => {
        const folderName = path.parse(folderSrc).base;
        fs.readdirSync(__dirname + folderSrc).forEach(file => {
            fs.mkdir(`${__dirname}/../SharedResources/Technician/${folderName}/`,()=> {})
            if (file.indexOf('.test.js') < 0) {
                fs.copyFile(path.normalize(__dirname + folderSrc + file), path.normalize(`${__dirname}/../SharedResources/Technician/${folderName}/${file}`), (err) => {
                    if (err) throw err;
                    console.log(`${path.parse(file).base} fiile was copied`);
                });
            } else {
                console.log(`${file} Excluded ${file.indexOf('.test.js')}`);  
            }
        });
    });
}

// Assisted by watsonx Code Assistant 
/**
 * Removes a folder and its contents.
 * @param {string[]} folderDelete - An array of folder paths to be removed.
 */
function removeFolder() {
    folderDelete.forEach(folderSrc => {
        if (fs.existsSync(`${__dirname}${folderSrc}`)) {
            // Remove the folder and its contents
            fs.rm(`${__dirname}${folderSrc}`, { recursive: true, force: true }, (error) => {
                //you can handle the error here
                if(error) {
                    console.log(`Error occured while remove directory: ${error} - ${__dirname}${folderSrc}`);
                }
            });
            console.log(`Removed folder: ${__dirname}${folderSrc}`);
        } else {
            console.log(`Test Folder not found: ${__dirname}${folderSrc} still it's working as expected`);
        }
    });
}

if(!process.env.CI) {
    shell.exec('git submodule update --init --recursive --remote');
    copyFileFolder();
    removeFolder();
}
