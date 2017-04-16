import arcpy


class Toolbox(object):
    def __init__(self):
        self.alias = "SOMToolbox"
        self.label = "SOM Toolbox"
        self.tools = [WKTTool]


class WKTTool(object):
    def __init__(self):
        self.label = "Import SOM"
        self.description = "Import SOM"
        self.canRunInBackground = True

    def getParameterInfo(self):
        param_file = arcpy.Parameter(
            name="in_name",
            displayName="Name",
            direction="Input",
            datatype="DEFile",
            parameterType="Required")
        param_file.value = "Z:\\Share\\Path.wkt"

        out_fc = arcpy.Parameter(
            name="som",
            displayName="SOM",
            direction="Output",
            datatype="Feature Layer",
            parameterType="Derived")

        return [param_file, out_fc]

    def isLicensed(self):
        return True

    def updateParameters(self, parameters):
        return

    def updateMessages(self, parameters):
        return

    def execute(self, parameters, messages):
        sp_ref = arcpy.SpatialReference(102100)
        fc = "in_memory/SOM"
        if arcpy.Exists(fc):
            arcpy.management.Delete(fc)
        arcpy.management.CreateFeatureclass("in_memory", "SOM", "POINT",
                                            spatial_reference=sp_ref,
                                            has_m="DISABLED",
                                            has_z="DISABLED")
        arcpy.management.AddField(fc, "WEIGHT", "FLOAT")
        arcpy.management.AddField(fc, "COL", "SHORT")
        arcpy.management.AddField(fc, "ROW", "SHORT")
        arcpy.management.AddField(fc, "CLASS_ID", "SHORT")
        with arcpy.da.InsertCursor(fc, ['SHAPE@X', 'SHAPE@Y', 'WEIGHT', 'COL', 'ROW', 'CLASS_ID']) as cursor:
            with open(parameters[0].valueAsText, "r") as f:
                for line in f:
                    cursor.insertRow(line.rstrip().split('\t'))

        parameters[1].value = fc
