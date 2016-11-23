<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:order="http://www.j-labs.pl/blog/order" xmlns="http://www.w3.org/1999/xhtml">
  <xsl:output method="xml" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd" doctype-public="-//W3C//DTD XHTML 1.1//EN" indent="yes"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Order confirmation</title>
      </head>
      <body>
        <p>Dear <xsl:value-of select="order:order/order:client/order:name"/><xsl:text> </xsl:text><xsl:value-of select="order:order/order:client/order:surname"/><xsl:text>,</xsl:text></p>
        <p>Thank you for shopping in our store! Your order has been completed and it is waiting for send.</p>
        <p>Order details:</p>
        <p>Delivery address:</p>
        <p><xsl:value-of select="order:order/order:client/order:name"/><xsl:text> </xsl:text><xsl:value-of select="order:order/order:client/order:surname"/><br/>
           <xsl:value-of select="order:order/order:client/order:address"/></p>
        <p>Ordered items:</p>
        <table>
          <thead>
            <tr>
              <th>Item</th>
              <th>Description</th>
              <th>Count</th>
            </tr>
          </thead>
          <tbody>
            <xsl:for-each select="order:order/order:items/order:item">
            <tr>
              <td><xsl:apply-templates select="order:name"/></td>
              <td><xsl:apply-templates select="order:description"/></td>
              <td><xsl:apply-templates select="order:count"/></td>
            </tr>
            </xsl:for-each>
          </tbody>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>