package net.barik.spreadsheet;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.tika.mime.MimeTypeException;

import com.amazonaws.AmazonClientException;

public class POIFastFilterMapper extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException {
        String resourceKey = value.toString();
        Configuration conf = context.getConfiguration();

        // Type can be s3 or local; s3 by default.
        String importType = conf.get("import.type", "s3");
        String exportType = conf.get("export.type", "s3");

        // Used when type is local.
        String importPath = conf.get("import.path", "./");
        String exportPath = conf.get("export.path", "./");

        // Used when type is s3.
        String importBucket = conf.get("import.bucket", "barik-cc");
        String importKeyPrefix = conf.get("import.keyprefix", "cc-warc-stage2/");

        String exportBucket = conf.get("export.bucket", "barik-cc");
        String exportKeyPrefix = conf.get("export.keyprefix", "cc-warc-stage3-filter/");

        String tmpDirectory = conf.get("tmp.directory", "/mnt/");

        RecordIO importIO;
        if (importType.equals("s3")) {
            importIO = new S3RecordIO(importBucket, importKeyPrefix, tmpDirectory);
        } else {
            importIO = new LocalRecordIO(importPath);
        }

        RecordIO exportIO;
        if (exportType.equals("s3")) {
            exportIO = new S3RecordIO(exportBucket, exportKeyPrefix, tmpDirectory);
        } else {
            exportIO = new LocalRecordIO(exportPath);
        }

        POIFastFilterModel model = new POIFastFilterModel(importIO, exportIO);

        while (true) {
            try {
                POIFastFilterDataModel dataModel = model.parse(resourceKey);

                if (POIFastFilterModel.isValidSpreadsheet(dataModel)) {
                    // Save the JSON data model.
                    model.export(dataModel);
                }
            }
            catch (MimeTypeException e) {
                throw new IOException(e.toString());
            }
            catch (AmazonClientException e) {
                e.printStackTrace();
                continue;
            }
            catch (SocketTimeoutException e) {
                e.printStackTrace();
                continue;
            }
            catch (SocketException e) {
                e.printStackTrace();
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            break;
        }
    }
}
