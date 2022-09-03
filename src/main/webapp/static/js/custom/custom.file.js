var FILE_HANDLE_ = {
    FORMAT_: {
        excel: ["xls", "xlsx"],
        image: ["jpg", "gif", "bmp", "png", "jpeg"],
        word: ["docx", "doc"],
        ppt: ["pptx", "ppt"],
        pdf: ["pdf"],
        compress: ["rar", "zip"]
    },
    checkNotNull: function (fileName) {
        if (null == fileName || fileName.length == 0) {
            return false;
        }
        return true;
    },
    getFileType: function (fileName) {
        return (fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)).toLowerCase();
    },
    checkFormat: function (fileTypes, fileFormat) {
        if (null == fileTypes || fileTypes.length == 0) {
            return false;
        }
        if (null == fileFormat || fileFormat.length == 0) {
            return false;
        }
        for (var i = 0; i < fileTypes.length; i++) {
            if (fileTypes[i] == fileFormat) {
                return true;
            }
        }
        return false;
    },
    checkFile: function (fileTypes, fileName) {
        if (FILE_HANDLE_.checkNotNull(fileName)) {
            var fileFormat = FILE_HANDLE_.getFileType(fileName);
            return FILE_HANDLE_.checkFormat(fileTypes, fileFormat);
        }
        return false;
    },
    checkExcel: function (fileName) {
        return FILE_HANDLE_.checkFile(FILE_HANDLE_.FORMAT_.excel, fileName);
    },
    checkImage: function (fileName) {
        return FILE_HANDLE_.checkFile(FILE_HANDLE_.FORMAT_.image, fileName);
    },
    checkWord: function (fileName) {
        return FILE_HANDLE_.checkFile(FILE_HANDLE_.FORMAT_.word, fileName);
    },
    checkPpt: function (fileName) {
        return FILE_HANDLE_.checkFile(FILE_HANDLE_.FORMAT_.ppt, fileName);
    },
    checkPdf: function (fileName) {
        return FILE_HANDLE_.checkFile(FILE_HANDLE_.FORMAT_.pdf, fileName);
    },
    checkCompress: function (fileName) {
        return FILE_HANDLE_.checkFile(FILE_HANDLE_.FORMAT_.compress, fileName);
    }
};
