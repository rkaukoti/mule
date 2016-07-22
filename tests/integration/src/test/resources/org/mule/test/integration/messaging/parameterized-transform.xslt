<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="2.0"
                xmlns="http://simple.component.mule.org/">

    <xsl:output method="xml" indent="no"/>

    <xsl:param name="prop"/>

    <xsl:template match="/">
        <root>
            <testval>
                <xsl:value-of select="$prop"/>
            </testval>
        </root>
    </xsl:template>
</xsl:stylesheet>
