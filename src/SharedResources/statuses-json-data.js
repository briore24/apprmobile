/*
 * Licensed Materials - Property of IBM
 *
 * 5724-U18, 5737-M66
 *
 * (C) Copyright IBM Corp. 2023 All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 */

let statusitem = {
  member: [
    {
      _rowstamp: '14303857',
      domainid: "POSTATUS",
      localref: 'oslc/os/mxapidomain/_V09TVEFUVVM-/synonymdomain/0-43',
      description: 'Approved',
      href:
        'https://childkey#TUFYRE9NQUlOL1NZTk9OWU1ET01BSU4vV09TVEFUVVMvQVBQUi9_TlVMTH4vfk5VTEx_L0FQUFI-',
      value: 'APPR',
      maxvalue: 'APPR',
      orgid: 'EAGLENA',
      siteid: 'BEDFORD',      
      defaults: true,
      valueid: 'POSTATUS|APPR'
    },
    {
      _rowstamp: '14303858',
      itemsetid: 'SET1',
      domainid: 'POSTATUS',
      localref: 'oslc/os/mxapidomain/_V09TVEFUVVM-/synonymdomain/1-3030',
      description: 'synonym 1 of APPR',
      href: 'https://childkey#TUFYRE9NQUlOL1NZTk9OWU1ET01BSU4vV09TVEFUVVMvQVBQUi9_TlVMTH4vfk5VTEx_L0FQUFIgUzE-',
      value: 'APPR S1',
      maxvalue: 'APPR',
      orgid: 'EAGLENA',
      siteid: 'BEDFORD',
      defaults: false,
    },
    {
      _rowstamp: '14303858',
      itemsetid: 'SET1',
      domainid: 'POSTATUS',
      localref: 'oslc/os/mxapidomain/_V09TVEFUVVM-/synonymdomain/1-3030',
      description: 'Cancelled',
      href: 'https://childkey#TUFYRE9NQUlOL1NZTk9OWU1ET01BSU4vV09TVEFUVVMvQVBQUi9_TlVMTH4vfk5VTEx_L0FQUFIgUzE-',
      value: 'CAN',
      maxvalue: 'CAN',
      orgid: 'EAGLENA',
      siteid: 'BEDFORD',
      defaults: true,
    },
    {
      _rowstamp: '16796669',
      itemsetid: 'SET1',
      localref: 'oslc/os/mxapidomain/_V09TVEFUVVM-/synonymdomain/10-42',
      domainid: 'POSTATUS',
      description: 'Waiting on approval',
      href: 'https://childkey#TUFYRE9NQUlOL1NZTk9OWU1ET01BSU4vV09TVEFUVVMvV0FQUFIvfk5VTEx_L35OVUxMfi9XQVBQUg--',
      value: 'WAPPR',
      maxvalue: 'WAPPR',
      defaults: true,
    },
    {
      _rowstamp: '16796681',
      itemsetid: 'SET1',
      localref: 'oslc/os/mxapidomain/_V09TVEFUVVM-/synonymdomain/10-43',
      domainid: 'POSTATUS',
      description: 'Inprogress',
      href: 'https://childkey#TUFYRE9NQUlOL1NZTk9OWU1ET01BSU4vV09TVEFUVVMvV0FQUFIvfk5VTEx_L35OVUxMfi9XQVBQUg--',
      value: 'INPRG',
      orgid: 'EAGLENA',
      siteid: 'BEDFORD',
      defaults: true,
      maxvalue: 'INPRG',
    },
    {
      defaults: true,
      itemsetid: 'SET1',
      description: 'Item',
      domainid: 'LOGTYPE',
      href: 'oslc/os/mxapisynonymdomain/_SVRFTVRZUEUvSVRFTS9_TlVMTH4vfk5VTEx_L0lURU0-',
      maxvalue: 'CLIENTNOTE',
      value: 'CLIENTNOTE',
      valueid: 'LOGTYPE|CLIENTNOTE',
      _bulkid: 'LOGTYPE|CLIENTNOTE',
      orgid: 'EAGLENA',
      siteid: 'BEDFORD',
    },
    {
      defaults: true,
      itemsetid: 'SET1',
      description: 'Item',
      domainid: 'LOGTYPE',
      href: 'oslc/os/mxapisynonymdomain/_SVRFTVRZUEUvSVRFTS9_TlVMTH4vfk5VTEx_L0lURU0-',
      maxvalue: 'APPTNOTE',
      value: 'APPTNOTE',
      valueid: 'LOGTYPE|APPTNOTE',
      _bulkid: 'LOGTYPE|APPTNOTE',
      orgid: '',
      siteid: 'BEDFORD',
    },
    {
      defaults: true,
      itemsetid: 'SET1',
      description: 'Item',
      domainid: 'LOGTYPE',
      href: 'oslc/os/mxapisynonymdomain/_SVRFTVRZUEUvSVRFTS9_TlVMTH4vfk5VTEx_L0lURU0-',
      maxvalue: 'NOTE',
      value: 'NOTE',
      valueid: 'LOGTYPE|NOTE',
      _bulkid: 'LOGTYPE|NOTE',
      orgid: '',
      siteid: '',
    },
    {
      valueid: 'ISSUETYP|ISSUE',
      itemsetid: 'SET1',
      maxvalue: 'ISSUE',
      defaults: true,
      description: 'Return item',
      href: 'oslc/os/mxapisynonymdomain/_SVNTVUVUWVAvUkVUVVJOL35OVUxMfi9_TlVMTH4vUkVUVVJO',
      value: 'ISSUE',
      domainid: 'ISSUETYP',
      MRSTATUS: 'MRSTATUS|DRAFT',
    },
  ],
  responseInfo: {
    schema: {
      href: 'oslc/os/mxapidomain/_V09TVEFUVVM-/synonymdomain?oslc.select=value%2Cdescription&oslc.pageSize=100&collectioncount=1&ignorecollectionref=1&relativeuri=1&addschema=1&lean=1&internalvalues=1',
      $ref: 'oslc/jsonschemas/mxapidomain/synonymdomain',
      $schema: 'https://json-schema.org/draft-04/schema#',
      description: 'MAXDOMAIN/SYNONYMDOMAIN',
      pk: ['domainid', 'maxvalue', 'value', 'siteid', 'orgid'],
      properties: {
        description: {
          searchType: 'WILDCARD',
          subType: 'ALN',
          title: 'Description',
          persistent: true,
          type: 'string',
        },
        href: { type: 'string' },
        localref: { type: 'string' },
        value: {
          searchType: 'WILDCARD',
          subType: 'ALN',
          title: 'Value',
          persistent: true,
          type: 'string',
        },
        maxvalue: {
          searchType: 'WILDCARD',
          subType: 'ALN',
          title: 'Value',
          persistent: true,
          type: 'string',
        },
        _imglibref: { type: 'string' },
        _rowstamp: { type: 'string' },
      },
      required: ['value'],
      resource: 'MXAPIDOMAIN',
      title: 'MAXDOMAIN/SYNONYMDOMAIN',
      type: 'object',
    },
  },
};

export default statusitem;
